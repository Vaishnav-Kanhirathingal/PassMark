package easter.egg.passmark.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordCapsule
import easter.egg.passmark.utils.security.PasswordCryptographyHandler

@Dao
interface PasswordDao {
    @Query("SELECT * FROM local_password_capsules")
    suspend fun getAll(): List<PasswordCapsule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(passwordCapsule: PasswordCapsule)

    @Query("Delete from local_password_capsules where local_id = :passwordId")
    suspend fun deleteById(passwordId: Int)
}

class PasswordTableHandler(
    private val passwordDao: PasswordDao,
    private val cryptographyHandler: PasswordCryptographyHandler
) {
    suspend fun getAll(): List<Password> {
        return passwordDao.getAll().map {
            it.toPassword(passwordCryptographyHandler = cryptographyHandler)
        }
    }

    suspend fun upsert(password: Password) {
        passwordDao.upsert(
            passwordCapsule = password.toPasswordCapsule(
                passwordCryptographyHandler = cryptographyHandler
            )
        )
    }

    suspend fun delete(localId: Int) {
        passwordDao.deleteById(passwordId = localId)
    }
}