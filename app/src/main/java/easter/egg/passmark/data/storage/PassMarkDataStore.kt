package easter.egg.passmark.data.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import easter.egg.passmark.utils.datastore
import easter.egg.passmark.utils.security.KeyStoreHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PassMarkDataStore(
    private val context: Context,
    authId: String
) {
    companion object {
        private val passwordKey = stringPreferencesKey(name = "password_key")
        private val initializationVectorKey = stringPreferencesKey(name = "i_v_key")
    }

    private val TAG = this::class.simpleName
    private val keyStoreHandler = KeyStoreHandler(authId = authId).apply { this.initialize() }

    //-----------------------------------------------------------------------------------------store
    suspend fun savePassword(password: String) {
        context.datastore.edit {
            val (encryptedPassword, iv) = keyStoreHandler.encrypt(password)
            it[passwordKey] = encryptedPassword
            it[initializationVectorKey] = iv
        }
    }

    suspend fun resetPassword() {
        context.datastore.edit {
            it.remove(passwordKey)
            it.remove(initializationVectorKey)
        }
    }

    //-----------------------------------------------------------------------------------------fetch
    fun fetchPassword(): Flow<String?> {
        return context.datastore.data
            .catch { it.printStackTrace() }
            .map {
                try {
                    val input = it[passwordKey]
                    val iv = it[initializationVectorKey]
                    if (input == null || iv == null) {
//                        Log.d(TAG, "input null = ${input == null}, iv null = ${iv == null}")
                        null
                    } else {
                        keyStoreHandler.decrypt(input = input, iv = iv)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
    }
}