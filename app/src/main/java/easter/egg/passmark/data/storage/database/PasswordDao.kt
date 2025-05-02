package easter.egg.passmark.data.storage.database

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import easter.egg.passmark.data.models.content.PasswordCapsule

@Dao
interface PasswordDao {
    @Query("SELECT * FROM local_password_capsules")
    suspend fun getAll(): List<PasswordCapsule>

    @Query("SELECT * FROM local_password_capsules WHERE local_id = :id")
    suspend fun getById(id: Int): PasswordCapsule

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(passwordCapsule: PasswordCapsule): Long

    @Query("Delete from local_password_capsules where local_id = :localId")
    suspend fun deleteById(localId: Int)

    @Query("Delete from local_password_capsules where vault_id = :vaultId")
    suspend fun deleteByVaultId(vaultId: Int)

    @Query("DELETE FROM local_password_capsules")
    suspend fun deleteAll()

    companion object {
        @Composable
        fun getTestingDao(): PasswordDao = Room
            .databaseBuilder(
                context = LocalContext.current,
                name = "sample",
                klass = PassMarkDatabase::class.java
            )
            .build()
            .passwordDao()
    }
}