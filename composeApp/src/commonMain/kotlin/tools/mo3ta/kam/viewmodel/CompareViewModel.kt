package tools.mo3ta.kam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tools.mo3ta.kam.data.City
import tools.mo3ta.kam.data.CityRepository
import tools.mo3ta.kam.analytics.logCitySelected
import tools.mo3ta.kam.analytics.logComparison

data class CompareUiState(
    val cities: List<City> = emptyList(),
    val city1: City? = null,
    val salary1: String = "",
    val city2: City? = null,
    val salary2: String = "",
    val rates: Map<String, Double> = emptyMap(),
    val result: CompareResult? = null
)

data class CompareResult(
    val normalized1: Double,
    val normalized2: Double,
    val winner: Int, // 1 or 2, 0 if equal
    val percentageBetter: Double
)

class CompareViewModel(
    private val repository: CityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.loadCities()
        }
        viewModelScope.launch {
            repository.cities.collect { cities ->
                _uiState.update { it.copy(cities = cities) }
            }
        }
        viewModelScope.launch {
            repository.rates.collect { rates ->
                _uiState.update { it.copy(rates = rates) }
                if (rates.isNotEmpty()) recalculate()
            }
        }
    }

    fun onCity1Selected(city: City) {
        _uiState.update { it.copy(city1 = city) }
        logCitySelected(city.name, "compare")
        recalculate()
    }

    fun onCity2Selected(city: City) {
        _uiState.update { it.copy(city2 = city) }
        logCitySelected(city.name, "compare")
        recalculate()
    }

    fun onSalary1Changed(salary: String) {
        val filtered = salary.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(salary1 = filtered) }
        recalculate()
    }

    fun onSalary2Changed(salary: String) {
        val filtered = salary.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(salary2 = filtered) }
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val c1 = state.city1 ?: return
        val c2 = state.city2 ?: return
        val rates = state.rates
        val s1 = state.salary1.toDoubleOrNull() ?: return
        val s2 = state.salary2.toDoubleOrNull() ?: return

        fun convertToUsd(amount: Double, currency: String): Double {
            if (rates.isEmpty()) return amount
            val rate = rates[currency] ?: 1.0
            return amount / rate
        }

        // Convert both salaries to a common currency (USD) to compare apples to apples
        val s1Usd = convertToUsd(s1, c1.currency)
        val s2Usd = convertToUsd(s2, c2.currency)

        // Normalize the USD salaries by the city's cost index
        val norm1 = s1Usd / c1.baseIndex
        val norm2 = s2Usd / c2.baseIndex

        val winner = when {
            norm1 > norm2 -> 1
            norm2 > norm1 -> 2
            else -> 0
        }

        val percentBetter = if (norm1 == 0.0 && norm2 == 0.0) {
            0.0
        } else {
            val max = maxOf(norm1, norm2)
            val min = minOf(norm1, norm2)
            if (min == 0.0) 100.0 else ((max - min) / min) * 100
        }

        _uiState.update {
            it.copy(
                result = CompareResult(
                    normalized1 = norm1,
                    normalized2 = norm2,
                    winner = winner,
                    percentageBetter = percentBetter
                )
            )
        }

        logComparison(c1.name, s1, c2.name, s2)
    }
}
