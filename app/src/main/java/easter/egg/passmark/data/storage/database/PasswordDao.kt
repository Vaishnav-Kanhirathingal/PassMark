package easter.egg.passmark.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
}