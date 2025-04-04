package easter.egg.passmark.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.Vault
import easter.egg.passmark.data.storage.PassMarkDataStore
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabaseAccountHelper: SupabaseAccountHelper,
    private val userApi: UserApi,
    private val vaultApi: VaultApi,
    private val passwordApi: PasswordApi
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _screenState: MutableStateFlow<ScreenState<HomeListingData>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<HomeListingData>> get() = _screenState

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

                val vaultListDeferred = async { vaultApi.getVaultList() }
                val passwordListDeferred =
                    async { passwordApi.getPasswordList(passwordCryptographyHandler = passwordCryptographyHandler) }
                ScreenState.Loaded(
                    result = HomeListingData(
                        vaultList = vaultListDeferred.await(),
                        passwordList = passwordListDeferred.await()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }.let { newState: ScreenState<HomeListingData> ->
                this@MainViewModel._screenState.value = newState
            }
        }
    }
}

data class HomeListingData(
    val vaultList: List<Vault>,
    val passwordList: List<Password>
)