package easter.egg.passmark.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

private typealias HomeList = Pair<List<Vault>, List<Password>>

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val supabaseAccountHelper: SupabaseAccountHelper,
    private val userApi: UserApi,
    private val vaultApi: VaultApi,
    private val passwordApi: PasswordApi
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _screenState: MutableStateFlow<ScreenState<HomeList>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<HomeList>> get() = _screenState

    lateinit var passwordCryptographyHandler: PasswordCryptographyHandler
        private set

    suspend fun refreshHomeList(): Unit = withContext(Dispatchers.IO) {
        try {
            val passwordCryptographyHandler = PasswordCryptographyHandler(
                password = PassMarkDataStore(
                    context = context,
                    authId = supabaseAccountHelper.getId()
                )
                    .fetchPassword()
                    .first()!!,
                initializationVector = userApi.getUser()!!.encryptionKeyInitializationVector
            )
            this@MainViewModel.passwordCryptographyHandler = passwordCryptographyHandler
            val vaultListDeferred = async { vaultApi.getVaultList() }
            val passwordListDeferred = async {
                passwordApi.getPasswordList(passwordCryptographyHandler = passwordCryptographyHandler)
            }
            ScreenState.Loaded(
                result = Pair(
                    first = vaultListDeferred.await(),
                    second = passwordListDeferred.await()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ScreenState.ApiError.fromException(e = e)
        }.let { newState ->
            this@MainViewModel._screenState.value = newState
        }
    }
}