package easter.egg.passmark.di.general

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import easter.egg.passmark.data.storage.SettingsDataStore

@Module
@InstallIn(ViewModelComponent::class)
class DataStoreModule {
    @Provides
    @ViewModelScoped
    fun providesSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore = SettingsDataStore(context = context)
}