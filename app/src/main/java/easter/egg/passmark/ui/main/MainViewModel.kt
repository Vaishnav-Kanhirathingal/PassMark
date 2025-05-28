package easter.egg.passmark.ui.main

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import easter.egg.passmark.data.models.Vault
import easter.egg.passmark.data.models.password.Password
import easter.egg.passmark.data.models.password.PasswordSortingOptions
import easter.egg.passmark.data.storage.PassMarkDataStore
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
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
                passwordDao = PasswordDao.getTestingDao()
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

    fun refreshHomeList(silentReload: Boolean = false) {
        if (!silentReload) {
            this._screenState.value = ScreenState.Loading()
        }
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

                val vaultListDeferred: Deferred<List<Vault>> = async {
                    vaultApi.getVaultList()
                }
                val remotePasswordDeferred: Deferred<List<Password>> = async {
                    passwordApi.getPasswordList(passwordCryptographyHandler = passwordCryptographyHandler)
                }
                val localPasswordDeferred: Deferred<List<Password>> = async {
                    passwordDao.getAll().mapNotNull { passwordCapsule ->
                        try {
                            passwordCapsule.toPassword(passwordCryptographyHandler = passwordCryptographyHandler)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                }
                ScreenState.Loaded(
                    result = HomeListData(
                        vaultList = vaultListDeferred.await().toMutableList(),
                        passwordList = remotePasswordDeferred.await().toMutableList()
                            .apply { this.addAll(elements = localPasswordDeferred.await()) }
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

    //-------------------------------------------------------------------------password-verification
    private val job = SupervisorJob()
    private val customScope = CoroutineScope(context = Dispatchers.IO + job)

    val passwordEntered: MutableStateFlow<String> = MutableStateFlow("")
    private val _passwordVerificationState: MutableStateFlow<ScreenState<Boolean>> =
        MutableStateFlow(ScreenState.PreCall())
    val passwordVerificationState: StateFlow<ScreenState<Boolean>> get() = _passwordVerificationState

    private var lockingTask: Job? = null

    fun startAppLockLambda() {
        lockingTask?.cancel()
        lockingTask = viewModelScope.launch {
            delay(timeMillis = 3_000)
            job.cancelChildren()
            this@MainViewModel._passwordVerificationState.value = ScreenState.PreCall()
            this@MainViewModel.passwordEntered.value = ""
            Log.d(TAG, "locked app")
        }
    }

    fun cancelAppLockLambda() {
        lockingTask?.cancel()
        lockingTask = null
    }

    fun forceVerify() {
        this._passwordVerificationState.value = ScreenState.Loaded(result = true)
    }

    fun verifyPassword() {
        val password: String = passwordEntered.value
        this._passwordVerificationState.value = ScreenState.Loading()
        customScope.launch {
            val newState = try {
                ScreenState.Loaded(
                    result = PasswordCryptographyHandler.verifyPassword(
                        password = password,
                        cryptographyHandler = passwordCryptographyHandler
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "failed offline verification")
                try {
                    userApi.getUser()!!.let { user ->
                        ScreenState.Loaded(
                            result = PasswordCryptographyHandler(
                                password = password,
                                initializationVector = user.encryptionKeyInitializationVector
                            ).solvesPuzzle(apiProvidedEncryptedPuzzle = user.passwordPuzzleEncrypted)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    ScreenState.ApiError.fromException(e = e)
                }
            }

            if ((newState as? ScreenState.Loaded)?.result == true) {
                this@MainViewModel.passwordEntered.value = ""
            }
            this@MainViewModel._passwordVerificationState.value = newState
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        customScope.cancel()
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

    @Composable
    fun getVaultById(vaultId: Int): Vault? {
        return this._vaultListState.collectAsState().value.find { vault: Vault -> vault.id == vaultId }
    }

    fun getFilteredPasswordList(
        vaultId: Int?,
        searchString: String?,
        passwordSortingOptions: PasswordSortingOptions,
        increasingOrder: Boolean
    ): Flow<List<Password>> {
        return this._passwordListState.map { list ->
            list
                .filter { p ->
                    (vaultId?.let { v -> v == p.vaultId } ?: true) &&
                            (searchString?.let { s ->
                                p.data.title.contains(other = s, ignoreCase = true)
                            } ?: true)
                }
                .let { passList ->
                    when (passwordSortingOptions) {
                        PasswordSortingOptions.NAME -> {
                            val selector = { password: Password -> password.data.title.lowercase() }
                            if (increasingOrder) passList.sortedBy(selector = selector)
                            else passList.sortedByDescending(selector)
                        }

                        PasswordSortingOptions.USAGE -> {
                            val selector = { password: Password -> password.usedCount }
                            if (!increasingOrder) passList.sortedBy(selector = selector)
                            else passList.sortedByDescending(selector)
                        }

                        PasswordSortingOptions.CREATED -> {
                            val selector = { password: Password -> password.created }
                            if (!increasingOrder) passList.sortedBy(selector = selector)
                            else passList.sortedByDescending(selector)
                        }

                        PasswordSortingOptions.LAST_USED -> {
                            val selector = { password: Password -> password.lastUsed }
                            if (!increasingOrder) passList.sortedBy(selector = selector)
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

    fun upsertNewVault(vault: Vault) {
        val tempList = this._vaultListState.value.toMutableList()
        val indexOfPresent = tempList.indexOfFirst { v -> v.id == vault.id }.takeUnless { it == -1 }

        if (indexOfPresent == null) tempList.add(element = vault)
        else tempList.set(index = indexOfPresent, element = vault)

        this._vaultListState.value = tempList
    }

    fun deletePassword(
        password: Password
    ) {
        val useCloudId = password.cloudId != null
        val useLocalId = password.localId != null
        this._passwordListState.value = this._passwordListState.value
            .toMutableList()
            .apply {
                this.removeIf {
                    if (useCloudId) {
                        it.cloudId == password.cloudId
                    } else if (useLocalId) {
                        it.localId == password.localId
                    } else {
                        false
                    }
                }
            }
    }

    /** deletes the vault and associated passwords from cache */
    fun deleteVaultAndAssociates(vaultId: Int) {
        this._passwordListState.value = this._passwordListState.value
            .toMutableList()
            .apply { removeAll { it.vaultId == vaultId } }
        this._vaultListState.value = this._vaultListState.value
            .toMutableList()
            .apply { removeAll { it.id == vaultId } }
    }
}