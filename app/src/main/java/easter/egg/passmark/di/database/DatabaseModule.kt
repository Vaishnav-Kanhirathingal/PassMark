package easter.egg.passmark.di.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import easter.egg.passmark.data.storage.database.PassMarkDatabase
import easter.egg.passmark.data.storage.database.PasswordDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun providesPassMarkDatabase(
        @ApplicationContext context: Context
    ): PassMarkDatabase {
        return Room.databaseBuilder(
            context = context,
            name = "passmark_database",
            klass = PassMarkDatabase::class.java
        ).build()
    }

    @Provides
    @Singleton
    fun providesPasswordDao(passMarkDatabase: PassMarkDatabase): PasswordDao {
        return passMarkDatabase.userDao()
    }
}