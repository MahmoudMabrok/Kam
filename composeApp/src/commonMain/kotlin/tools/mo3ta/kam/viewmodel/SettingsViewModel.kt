package tools.mo3ta.kam.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tools.mo3ta.kam.data.PreferenceManager

class SettingsViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(preferenceManager.isDarkMode)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _locale = MutableStateFlow(preferenceManager.locale)
    val locale = _locale.asStateFlow()

    fun toggleDarkMode() {
        preferenceManager.isDarkMode = !preferenceManager.isDarkMode
        _isDarkMode.value = preferenceManager.isDarkMode
    }

    fun setLocale(lang: String) {
        preferenceManager.locale = lang
        _locale.value = lang
    }
}
