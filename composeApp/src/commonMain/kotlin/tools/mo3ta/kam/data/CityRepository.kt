package tools.mo3ta.kam.data

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class CityRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val httpClient = HttpClient()
    private val settings = Settings()
    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: Flow<List<City>> = _cities.asStateFlow()

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: Flow<Map<String, Double>> = _rates.asStateFlow()

    suspend fun loadCities() {
        if (_cities.value.isNotEmpty()) return // already cached in memory

        try {
            val cachedData = settings.getStringOrNull("cities_data")
            if (!cachedData.isNullOrEmpty()) {
                val costData = json.decodeFromString<CostData>(cachedData)
                _cities.value = costData.cities
                // Even if cities are loaded from cache, still try to fetch rates live
                // and potentially update cities from network if cache is old or network is available
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If cities are still empty or we want to refresh from network
        if (_cities.value.isEmpty()) {
            try {
                val text = httpClient.get("https://mahmoudmabrok.github.io/Kam/data.json").bodyAsText()
                val costData = json.decodeFromString<CostData>(text)
                _cities.value = costData.cities
                settings.putString("cities_data", text)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            val cachedRates = settings.getStringOrNull("rates_data")
            if (!cachedRates.isNullOrEmpty()) {
                val ratesData = json.decodeFromString<ExchangeRatesResponse>(cachedRates)
                _rates.value = ratesData.rates
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            // Fetch live exchange rates to normalize currencies
            val ratesText = httpClient.get("https://open.er-api.com/v6/latest/USD").bodyAsText()
            val ratesData = json.decodeFromString<ExchangeRatesResponse>(ratesText)
            _rates.value = ratesData.rates
            settings.putString("rates_data", ratesText)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCityById(id: String): City? {
        return _cities.value.find { it.id == id }
    }

    fun getAllCities(): List<City> = _cities.value
}
