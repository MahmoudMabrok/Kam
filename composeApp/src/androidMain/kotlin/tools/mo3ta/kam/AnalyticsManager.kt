package tools.mo3ta.kam

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

/**
 * Centralized analytics & crash-reporting manager.
 * Wraps Firebase Analytics and Crashlytics behind a clean API.
 */
object AnalyticsManager {

    private const val TAG = "AnalyticsManager"

    private lateinit var analytics: FirebaseAnalytics

    /** Call once from [MainActivity.onCreate]. */
    fun initialize() {
        analytics = Firebase.analytics
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
        Log.d(TAG, "Firebase Analytics & Crashlytics initialized")
    }

    // ── Screen tracking ──────────────────────────────────────────────────

    fun logScreenView(screenName: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        Log.d(TAG, "screen_view: $screenName")
    }

    // ── Tab navigation ───────────────────────────────────────────────────

    fun logTabSwitch(tabName: String) {
        val params = Bundle().apply {
            putString("tab_name", tabName)
        }
        analytics.logEvent("tab_switch", params)
        Log.d(TAG, "tab_switch: $tabName")
    }

    // ── City search ──────────────────────────────────────────────────────

    fun logSearch(query: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SEARCH, params)
        Log.d(TAG, "search: $query")
    }

    // ── City selection ────────────────────────────────────────────────────

    fun logCitySelected(cityName: String, screen: String) {
        val params = Bundle().apply {
            putString("city_name", cityName)
            putString("screen", screen)
        }
        analytics.logEvent("city_selected", params)
        Log.d(TAG, "city_selected: $cityName on $screen")
    }

    // ── City card expand ─────────────────────────────────────────────────

    fun logCityCardExpanded(cityName: String, country: String) {
        val params = Bundle().apply {
            putString("city_name", cityName)
            putString("country", country)
        }
        analytics.logEvent("city_card_expanded", params)
        Log.d(TAG, "city_card_expanded: $cityName, $country")
    }

    // ── Swap cities ──────────────────────────────────────────────────────

    fun logSwapCities(fromCity: String, toCity: String) {
        val params = Bundle().apply {
            putString("from_city", fromCity)
            putString("to_city", toCity)
        }
        analytics.logEvent("swap_cities", params)
        Log.d(TAG, "swap_cities: $fromCity ⇄ $toCity")
    }

    // ── Salary conversion ────────────────────────────────────────────────

    fun logConversion(fromCity: String, toCity: String, salary: Double) {
        val params = Bundle().apply {
            putString("from_city", fromCity)
            putString("to_city", toCity)
            putDouble("salary", salary)
        }
        analytics.logEvent("salary_conversion", params)
        Log.d(TAG, "salary_conversion: $fromCity → $toCity ($salary)")
    }

    // ── Salary comparison ────────────────────────────────────────────────

    fun logComparison(city1: String, salary1: Double, city2: String, salary2: Double) {
        val params = Bundle().apply {
            putString("city_1", city1)
            putDouble("salary_1", salary1)
            putString("city_2", city2)
            putDouble("salary_2", salary2)
        }
        analytics.logEvent("salary_comparison", params)
        Log.d(TAG, "salary_comparison: $city1 ($salary1) vs $city2 ($salary2)")
    }

    // ── Generic custom event ─────────────────────────────────────────────

    fun logEvent(eventName: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) -> putString(key, value) }
        }
        analytics.logEvent(eventName, bundle)
        Log.d(TAG, "event: $eventName $params")
    }

    // ── User properties ──────────────────────────────────────────────────

    fun setUserProperty(key: String, value: String) {
        analytics.setUserProperty(key, value)
        Log.d(TAG, "user_property: $key=$value")
    }

    // ── Error / non-fatal logging (Crashlytics) ──────────────────────────

    fun logError(message: String, throwable: Throwable? = null) {
        Firebase.crashlytics.log(message)
        throwable?.let { Firebase.crashlytics.recordException(it) }
        Log.e(TAG, "error: $message", throwable)
    }
}
