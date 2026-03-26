package tools.mo3ta.kam.analytics

actual fun logTabSwitch(tabName: String) {}

actual fun logScreenView(screenName: String) {}

actual fun logSearch(query: String) {}

actual fun logCitySelected(cityName: String, screen: String) {}

actual fun logCityCardExpanded(cityName: String, country: String) {}

actual fun logSwapCities(fromCity: String, toCity: String) {}

actual fun logConversion(fromCity: String, toCity: String, salary: Double) {}

actual fun logComparison(city1: String, salary1: Double, city2: String, salary2: Double) {}
