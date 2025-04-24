package easter.egg.passmark.ui.main

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordSortingOptions
import easter.egg.passmark.data.models.content.Vault
import easter.egg.passmark.data.storage.PassMarkDataStore
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabaseAccountHelper: SupabaseAccountHelper,
    private val userApi: UserApi,
    private val vaultApi: VaultApi,
    private val passwordApi: PasswordApi,
    private val passwordDao: PasswordDao
) : ViewModel() {
    private val TAG = this::class.simpleName

    companion object {
        @Composable
        fun getTestViewModel(): MainViewModel {
            val supabaseClient = SupabaseModule.mockClient
            return MainViewModel(
                context = LocalContext.current,
                supabaseAccountHelper = SupabaseAccountHelper(supabaseClient),
                userApi = UserApi(supabaseClient),
                vaultApi = VaultApi(supabaseClient),
                passwordApi = PasswordApi(supabaseClient),
                passwordDao = PasswordDao.getDao()
            )
        }
    }

    private val _screenState: MutableStateFlow<ScreenState<HomeListData>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<HomeListData>> get() = _screenState

    lateinit var passwordCryptographyHandler: PasswordCryptographyHandler
        private set

    init {
        refreshHomeList()
    }

    fun refreshHomeList() {
        this._screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            try {
                val user = userApi.getUser()!!
                val password = PassMarkDataStore(
                    context = context,
                    authId = supabaseAccountHelper.getId()
                ).fetchPassword().first()!!

                val passwordCryptographyHandler = PasswordCryptographyHandler(
                    password = password,
                    initializationVector = user.encryptionKeyInitializationVector
                ).takeIf { it.solvesPuzzle(user.passwordPuzzleEncrypted) }!!

                this@MainViewModel.passwordCryptographyHandler = passwordCryptographyHandler

                val vaultListDeferred = async {
                    vaultApi.getVaultList()
                }
                val passwordListDeferred: Deferred<List<Password>> = async {
                    passwordApi.getPasswordList(passwordCryptographyHandler = passwordCryptographyHandler)
                }
                val localPasswordFetcher: Deferred<List<Password>> = async {
                    passwordDao.getAll().map { passwordCapsule ->
                        passwordCapsule.toPassword(passwordCryptographyHandler = passwordCryptographyHandler)
                    }
                }
                ScreenState.Loaded(
                    result = HomeListData(
                        vaultList = vaultListDeferred.await().toMutableList(),
                        passwordList = passwordListDeferred.await().toMutableList()
                            .apply { this.addAll(elements = localPasswordFetcher.await()) }
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }.let { newState: ScreenState<HomeListData> ->
                this@MainViewModel._screenState.value = newState
            }
        }
    }
}

class HomeListData(
    vaultList: MutableList<Vault>,
    passwordList: MutableList<Password>
) {
    private val TAG = this::class.simpleName

    private val _vaultListState: MutableStateFlow<List<Vault>> = MutableStateFlow(vaultList)
    val vaultListState: StateFlow<List<Vault>> = _vaultListState

    private val _passwordListState: MutableStateFlow<List<Password>> =
        MutableStateFlow(passwordList)
    val passwordListState: StateFlow<List<Password>> get() = _passwordListState

    fun getFilteredPasswordList(
        vaultId: Int?,
        passwordSortingOptions: PasswordSortingOptions,
        ascending: Boolean
    ): Flow<List<Password>> {
        return this._passwordListState.map { list ->
            list
                .filter { password: Password ->
                    vaultId?.let { v -> v == password.vaultId } ?: true
                }
                .let { passList ->
                    when (passwordSortingOptions) {
                        PasswordSortingOptions.NAME -> {
                            val selector = { password: Password -> password.data.title }
                            if (ascending) passList.sortedBy(selector = selector)
                            else passList.sortedByDescending(selector)
                        }

                        PasswordSortingOptions.USAGE -> {
                            val selector = { password: Password -> password.usedCount }
                            if (ascending) passList.sortedBy(selector = selector)
                            else passList.sortedByDescending(selector)
                        }

                        PasswordSortingOptions.CREATED -> {
                            val selector = { password: Password -> password.created }
                            if (ascending) passList.sortedBy(selector = selector)
                            else passList.sortedByDescending(selector)
                        }

                        PasswordSortingOptions.LAST_USED -> {
                            val selector = { password: Password -> password.lastUsed }
                            if (ascending) passList.sortedBy(selector = selector)
                            else passList.sortedByDescending(selector)
                        }
                    }
                }
        }
    }

    fun upsertPassword(password: Password) {
        val newList = this._passwordListState.value.toMutableList()

        val useCloudId = password.cloudId != null
        newList
            .indexOfLast { p ->
                if (useCloudId) {
                    p.cloudId == password.cloudId
                } else {
                    p.localId == password.localId
                }
            }
            .takeUnless { it == -1 }
            .let {
                if (it == null) {
                    newList.add(password)
                    Log.d(TAG, "password is new, adding")
                } else {
                    newList.set(index = it, element = password)
                    Log.d(TAG, "password already exists, updating")
                }
            }
        this._passwordListState.value = newList
    }

    fun addNewVault(vault: Vault) {
        this._vaultListState.value =
            this._vaultListState.value.toMutableList().apply { add(vault) }
    }

    fun deletePassword(
        password: Password
    ) {
        val useCloudId = password.cloudId != null
        this._passwordListState.value = this._passwordListState.value
            .toMutableList()
            .apply {
                this.removeIf {
                    if (useCloudId) {
                        it.cloudId == password.cloudId
                    } else {
                        it.localId == password.localId
                    }
                }
            }
    }
}