package tools.mo3ta.kam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tools.mo3ta.kam.analytics.logCitySelected
import tools.mo3ta.kam.analytics.logComparison
import tools.mo3ta.kam.data.City
import tools.mo3ta.kam.data.CityRepository
import tools.mo3ta.kam.data.PreferenceManager
import tools.mo3ta.kam.domain.CompareResult
import tools.mo3ta.kam.domain.compare

enum class RevampScreen { HOME, RESULT }
enum class PickerRole { ORIGIN, DEST }
enum class KeypadTarget { SALARY, OFFER }

data class RevampUiState(
    val loading: Boolean = true,
    val cities: List<City> = emptyList(),
    val rates: Map<String, Double> = emptyMap(),
    val origin: City? = null,
    val dest: City? = null,
    val salaryStr: String = "",
    val offerStr: String = "",
    val screen: RevampScreen = RevampScreen.HOME,
    val picker: PickerRole? = null,
    val keypad: KeypadTarget? = null,
) {
    val salary: Double get() = salaryStr.toDoubleOrNull() ?: 0.0
    val offer: Double get() = offerStr.toDoubleOrNull() ?: 0.0

    /** Live comparison; null until both cities are known. */
    val result: CompareResult?
        get() {
            val o = origin ?: return null
            val d = dest ?: return null
            return compare(o, d, salary, offer, rates)
        }
}

class RevampViewModel(
    private val repository: CityRepository,
    private val prefs: PreferenceManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RevampUiState())
    val uiState: StateFlow<RevampUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { repository.loadCities() }
        viewModelScope.launch {
            repository.cities.collect { cities ->
                _uiState.update { st -> seedIfNeeded(st.copy(cities = cities, loading = cities.isEmpty())) }
            }
        }
        viewModelScope.launch {
            repository.rates.collect { rates ->
                _uiState.update { it.copy(rates = rates) }
            }
        }
    }

    /** Resolve persisted/default origin+dest+salary once cities are available. */
    private fun seedIfNeeded(st: RevampUiState): RevampUiState {
        if (st.cities.isEmpty()) return st
        if (st.origin != null && st.dest != null) return st
        val byId = st.cities.associateBy { it.id }
        val origin = byId[prefs.originId] ?: st.cities.first()
        // Prefer a default destination in a *different* currency so the first
        // comparison actually exercises FX conversion (more meaningful default).
        val dest = byId[prefs.destId]?.takeIf { it.id != origin.id }
            ?: st.cities.firstOrNull { it.id != origin.id && it.currency != origin.currency }
            ?: st.cities.firstOrNull { it.id != origin.id }
            ?: origin
        val salary = prefs.salaryStr
            ?: origin.summary.avgSalary?.let { if (it > 0) it.toLong().toString() else null }
            ?: "3000"
        return st.copy(origin = origin, dest = dest, salaryStr = salary, loading = false)
    }

    fun openPicker(role: PickerRole) = _uiState.update { it.copy(picker = role) }
    fun closePicker() = _uiState.update { it.copy(picker = null) }

    fun selectCity(id: String) {
        val st = _uiState.value
        val city = st.cities.firstOrNull { it.id == id } ?: return
        val (origin, dest) = when (st.picker) {
            PickerRole.ORIGIN ->
                if (city.id == st.dest?.id) city to st.origin!! else city to st.dest
            PickerRole.DEST ->
                if (city.id == st.origin?.id) st.dest!! to city else st.origin to city
            null -> st.origin to st.dest
        }
        logCitySelected(city.name, "compare")
        _uiState.update { it.copy(origin = origin, dest = dest, picker = null) }
        persistRoute()
    }

    fun swap() {
        _uiState.update { it.copy(origin = it.dest, dest = it.origin) }
        persistRoute()
    }

    fun openKeypad(target: KeypadTarget) = _uiState.update { it.copy(keypad = target) }
    fun closeKeypad() {
        _uiState.update { it.copy(keypad = null) }
        prefs.salaryStr = _uiState.value.salaryStr
    }

    fun setKeypadValue(value: String) {
        _uiState.update {
            when (it.keypad) {
                KeypadTarget.OFFER -> it.copy(offerStr = value)
                else -> it.copy(salaryStr = value)
            }
        }
    }

    fun clearOffer() = _uiState.update { it.copy(offerStr = "") }

    fun goToResult() {
        val st = _uiState.value
        // Don't navigate until FX rates have loaded — otherwise the comparison
        // would compute with a 1:1 currency fallback and show wrong numbers.
        if (st.result?.ratesReady != true) return
        st.origin?.let { o -> st.dest?.let { d -> logComparison(o.name, st.salary, d.name, st.offer) } }
        _uiState.update { it.copy(screen = RevampScreen.RESULT) }
    }

    fun goHome() = _uiState.update { it.copy(screen = RevampScreen.HOME) }

    private fun persistRoute() {
        val st = _uiState.value
        prefs.originId = st.origin?.id
        prefs.destId = st.dest?.id
    }
}
