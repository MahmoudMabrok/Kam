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
import tools.mo3ta.kam.analytics.logConversion
import tools.mo3ta.kam.analytics.logSwapCities

enum class LifestyleLevel(val label: String, val emoji: String) {
    BASIC("Basic", "🏠"),
    MIDDLE("Middle", "🏡"),
    COMFORTABLE("Comfortable", "🏘️"),
    LUXURY("Luxury", "🏰")
}

/**
 * Full picture of what a salary means in two cities.
 *
 * Key insight:
 * - [normalizedFrom]  = salary / from.baseIndex  → real purchasing units in FROM city
 * - [normalizedIfMoved] = salary / to.baseIndex   → purchasing units you'd have IF you moved
 *   with the SAME salary (no raise)
 * - [equivalentSalary] = salary * (to.baseIndex / from.baseIndex) → salary you'd need in the
 *   destination to maintain the EXACT SAME lifestyle
 * - [lifestyleDelta]   = normalizedIfMoved - normalizedFrom  (negative = lifestyle drops)
 */
data class ConversionResult(
    // Raw numbers
    val equivalentSalary: Double,          // salary needed in TO city for same lifestyle
    val normalizedFrom: Double,            // salary/from.baseIndex → purchasing units now
    val normalizedIfMoved: Double,         // salary/to.baseIndex  → units if moved with same pay
    // % change in real purchasing power when moving with the SAME salary
    val purchasingPowerChangePct: Double,  // negative means lifestyle drops
    // % difference between original and equivalent (cost gap)
    val costOfLivingGapPct: Double,        // positive = destination is more expensive
    val fromLifestyle: LifestyleLevel,
    val toLifestyle: LifestyleLevel,       // lifestyle tier at equivalent salary in destination
    val movedLifestyle: LifestyleLevel     // lifestyle tier IF you moved with same salary
)

data class ConvertUiState(
    val cities: List<City> = emptyList(),
    val fromCity: City? = null,
    val toCity: City? = null,
    val rates: Map<String, Double> = emptyMap(),
    val salary: String = "",
    val result: ConversionResult? = null
)

class ConvertViewModel(
    private val repository: CityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConvertUiState())
    val uiState: StateFlow<ConvertUiState> = _uiState.asStateFlow()

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

    fun onFromCitySelected(city: City) {
        _uiState.update { it.copy(fromCity = city) }
        logCitySelected(city.name, "convert")
        recalculate()
    }

    fun onToCitySelected(city: City) {
        _uiState.update { it.copy(toCity = city) }
        logCitySelected(city.name, "convert")
        recalculate()
    }

    fun onSalaryChanged(salary: String) {
        val filtered = salary.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(salary = filtered) }
        recalculate()
    }

    fun swapCities() {
        val state = _uiState.value
        _uiState.update { it.copy(fromCity = it.toCity, toCity = it.fromCity) }
        logSwapCities(
            fromCity = state.fromCity?.name ?: "none",
            toCity = state.toCity?.name ?: "none"
        )
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val from = state.fromCity ?: return
        val to = state.toCity ?: return
        val rates = state.rates
        val salary = state.salary.toDoubleOrNull() ?: return
        if (salary <= 0) return

        fun convertCurrency(amount: Double, fromC: String, toC: String): Double {
            if (fromC == toC || rates.isEmpty()) return amount
            val rateFrom = rates[fromC] ?: 1.0
            val rateTo = rates[toC] ?: 1.0
            return (amount / rateFrom) * rateTo
        }

        // Convert salary to target currency directly (for checking moved lifestyle)
        val salaryInToCurrency = convertCurrency(salary, from.currency, to.currency)

        // For cost of living, we normalize via USD to ensure mathematical parity independent of currency value
        val salaryUsd = convertCurrency(salary, from.currency, "USD")

        // How much does this salary "buy" in the from city (normalized units, using USD base)
        val normalizedFrom = salaryUsd / from.baseIndex

        // What those same units would cost to maintain in the destination (in USD)
        val equivalentSalaryUsd = salaryUsd * (to.baseIndex / from.baseIndex)
        
        // The equivalent salary in the destination currency
        val equivalentSalaryToCurrency = convertCurrency(equivalentSalaryUsd, "USD", to.currency)

        // If the user moved with THE SAME salary (converted to TO currency) — how many purchasing units do they get?
        val normalizedIfMoved = salaryUsd / to.baseIndex

        // % change in real purchasing power when moving with the same salary
        val purchasingPowerChangePct = ((normalizedIfMoved - normalizedFrom) / normalizedFrom) * 100

        // % gap between original salary and the equivalent needed (both evaluated in USD for fair comparison)
        val costOfLivingGapPct = ((equivalentSalaryUsd - salaryUsd) / salaryUsd) * 100

        // Lifestyle calculations must be done using the currency of that specific city
        val fromLifestyle = calculateLifestyle(salary, from)
        val toLifestyle = calculateLifestyle(equivalentSalaryToCurrency, to) // same lifestyle, new city
        val movedLifestyle = calculateLifestyle(salaryInToCurrency, to)      // same money, new city

        _uiState.update {
            it.copy(
                result = ConversionResult(
                    equivalentSalary = equivalentSalaryToCurrency,
                    normalizedFrom = normalizedFrom,
                    normalizedIfMoved = normalizedIfMoved,
                    purchasingPowerChangePct = purchasingPowerChangePct,
                    costOfLivingGapPct = costOfLivingGapPct,
                    fromLifestyle = fromLifestyle,
                    toLifestyle = toLifestyle,
                    movedLifestyle = movedLifestyle
                )
            )
        }

        logConversion(from.name, to.name, salary)
    }

    companion object {
        fun calculateLifestyle(salary: Double, city: City): LifestyleLevel {
            val ratio = salary / city.summary.singleMonthly
            return when {
                ratio < 1.0 -> LifestyleLevel.BASIC
                ratio < 2.0 -> LifestyleLevel.MIDDLE
                ratio < 3.5 -> LifestyleLevel.COMFORTABLE
                else -> LifestyleLevel.LUXURY
            }
        }
    }
}
