package tools.mo3ta.kam.data

import kotlinx.serialization.Serializable

@Serializable
data class CostData(
    val version: Int,
    val lastUpdated: String,
    val cities: List<City>
)

@Serializable
data class City(
    val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val currency: String,
    val baseIndex: Double,
    val indices: Indices,
    val summary: Summary
)

@Serializable
data class Indices(
    val costOfLiving: Double,
    val rent: Double,
    val groceries: Double,
    val restaurant: Double,
    val purchasingPower: Double
)

@Serializable
data class Summary(
    val singleMonthly: Double,
    val familyMonthly: Double,
    val avgSalary: Double? = null
)

@Serializable
data class ExchangeRatesResponse(
    val rates: Map<String, Double>
)
