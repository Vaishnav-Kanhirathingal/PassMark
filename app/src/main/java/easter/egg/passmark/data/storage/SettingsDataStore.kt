package easter.egg.passmark.data.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import easter.egg.passmark.data.models.password.PasswordSortingOptions
import easter.egg.passmark.utils.datastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SettingsDataStore(
    private val context: Context
) {
    companion object {
        private val biometricsEnabledByDefaultKey =
            booleanPreferencesKey("biometrics_enabled_by_default")
        private val offlineStorageEnabledByDefaultKey =
            booleanPreferencesKey("offlineStorage_enabled_by_default")
        private val sortingOptionKey = stringPreferencesKey("sorting_option")
        private val isIncreasingOrderKey = booleanPreferencesKey("sorting_order")
    }

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

    suspend fun setSortingOption(passwordSortingOptions: PasswordSortingOptions) {
        context.datastore.edit {
            it[sortingOptionKey] = passwordSortingOptions.name
        }
    }

    suspend fun setIncreasingOrder(increasing: Boolean) {
        context.datastore.edit {
            it[isIncreasingOrderKey] = increasing
        }
    }
    //-----------------------------------------------------------------------------------------fetch

    private val catcher = context.datastore.data
        .catch { it.printStackTrace() }

    fun getBiometricEnabledFlow(): Flow<Boolean> {
        return catcher.map { it[biometricsEnabledByDefaultKey] ?: false }
    }

    fun getOfflineStorageFlow(): Flow<Boolean> {
        return catcher.map { it[offlineStorageEnabledByDefaultKey] ?: false }
    }

    fun getSortingOptionFlow(): Flow<PasswordSortingOptions> {
        return catcher.map {
            try {
                PasswordSortingOptions.valueOf(value = it[sortingOptionKey]!!)
            } catch (e: Exception) {
                e.printStackTrace()
                PasswordSortingOptions.CREATED
            }
        }
    }

    fun getIsIncreasingOrder(): Flow<Boolean> {
        return catcher.map { it[isIncreasingOrderKey] ?: true }
    }
}