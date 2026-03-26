package tools.mo3ta.kam.analytics

/** Cross-platform analytics bridge — actual implementations per target. */

expect fun logTabSwitch(tabName: String)

expect fun logScreenView(screenName: String)

expect fun logSearch(query: String)

expect fun logCitySelected(cityName: String, screen: String)

expect fun logCityCardExpanded(cityName: String, country: String)

expect fun logSwapCities(fromCity: String, toCity: String)

expect fun logConversion(fromCity: String, toCity: String, salary: Double)

expect fun logComparison(city1: String, salary1: Double, city2: String, salary2: Double)
