package easter.egg.passmark.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import easter.egg.passmark.data.models.password.PasswordCapsule

@Database(entities = [PasswordCapsule::class], version = 1)
abstract class PassMarkDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
}