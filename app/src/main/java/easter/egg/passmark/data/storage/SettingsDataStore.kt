package easter.egg.passmark.data.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import easter.egg.passmark.utils.datastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SettingsDataStore(
    private val context: Context,
) {
    companion object {
        private val biometricsEnabledByDefaultKey =
            booleanPreferencesKey("biometrics_enabled_by_default")
        private val offlineStorageEnabledByDefaultKey =
            booleanPreferencesKey("offlineStorage_enabled_by_default")
    }

    private val TAG = this::class.simpleName

    //-----------------------------------------------------------------------------------------store
    suspend fun changeBiometricsPreference(
        biometricsEnabledByDefault: Boolean,
    ) {
        context.datastore.edit {
            it[biometricsEnabledByDefaultKey] = biometricsEnabledByDefault
        }
    }

    suspend fun changeOfflineStoragePreference(
        offlineStorageEnabledByDefault: Boolean
    ) {
        context.datastore.edit {
            it[offlineStorageEnabledByDefaultKey] = offlineStorageEnabledByDefault
        }
    }

    //-----------------------------------------------------------------------------------------fetch

    fun getBiometricEnabledFlow(): Flow<Boolean> {
        return context.datastore.data
            .catch { it.printStackTrace() }
            .map {
                it[biometricsEnabledByDefaultKey] ?: false
            }
    }

    fun getOfflineStorageFlow(): Flow<Boolean> {
        return context.datastore.data
            .catch { it.printStackTrace() }
            .map {
                it[offlineStorageEnabledByDefaultKey] ?: false
            }
    }
}