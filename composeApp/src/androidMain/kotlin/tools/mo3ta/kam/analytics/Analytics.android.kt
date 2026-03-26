package tools.mo3ta.kam.analytics

import tools.mo3ta.kam.AnalyticsManager

actual fun logTabSwitch(tabName: String) = AnalyticsManager.logTabSwitch(tabName)

actual fun logScreenView(screenName: String) = AnalyticsManager.logScreenView(screenName)

actual fun logSearch(query: String) = AnalyticsManager.logSearch(query)

actual fun logCitySelected(cityName: String, screen: String) =
    AnalyticsManager.logCitySelected(cityName, screen)

actual fun logCityCardExpanded(cityName: String, country: String) =
    AnalyticsManager.logCityCardExpanded(cityName, country)

actual fun logSwapCities(fromCity: String, toCity: String) =
    AnalyticsManager.logSwapCities(fromCity, toCity)

actual fun logConversion(fromCity: String, toCity: String, salary: Double) =
    AnalyticsManager.logConversion(fromCity, toCity, salary)

actual fun logComparison(city1: String, salary1: Double, city2: String, salary2: Double) =
    AnalyticsManager.logComparison(city1, salary1, city2, salary2)

