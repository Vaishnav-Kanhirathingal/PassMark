package easter.egg.passmark.ui.main.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.storage.SettingsDataStore
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
     val settingsDataStore: SettingsDataStore
) : ViewModel()