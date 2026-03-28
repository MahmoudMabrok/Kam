package tools.mo3ta.kam.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class PreferenceManager(private val settings: Settings = Settings()) {
    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LOCALE = "locale"
        private const val KEY_ONBOARDED = "onboarded"
    }

    var isDarkMode: Boolean
        get() = settings[KEY_DARK_MODE, false]
        set(value) { settings[KEY_DARK_MODE] = value }

    var locale: String
        get() = settings[KEY_LOCALE, "en"]
        set(value) { settings[KEY_LOCALE] = value }

    var isOnboarded: Boolean
        get() = settings[KEY_ONBOARDED, false]
        set(value) { settings[KEY_ONBOARDED] = value }
}
