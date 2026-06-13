package tools.mo3ta.kam.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class PreferenceManager(private val settings: Settings = Settings()) {
    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LOCALE = "locale"
        private const val KEY_ONBOARDED = "onboarded"
        private const val KEY_ORIGIN = "origin_id"
        private const val KEY_DEST = "dest_id"
        private const val KEY_SALARY = "salary_str"
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

    var originId: String?
        get() = settings.getStringOrNull(KEY_ORIGIN)
        set(value) { if (value != null) settings[KEY_ORIGIN] = value }

    var destId: String?
        get() = settings.getStringOrNull(KEY_DEST)
        set(value) { if (value != null) settings[KEY_DEST] = value }

    var salaryStr: String?
        get() = settings.getStringOrNull(KEY_SALARY)
        set(value) { if (value != null) settings[KEY_SALARY] = value }
}
