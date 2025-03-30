package easter.egg.passmark.utils

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PassMarkApp : Application()

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "passmark_datastore")