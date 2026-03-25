package tools.mo3ta.kam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tools.mo3ta.kam.data.City
import tools.mo3ta.kam.data.CityRepository

class CityListViewModel(
    private val repository: CityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val groupedCities: StateFlow<Map<String, List<City>>> = combine(
        repository.cities,
        _searchQuery
    ) { cities, query ->
        val filtered = if (query.isBlank()) {
            cities
        } else {
            cities.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.country.contains(query, ignoreCase = true)
            }
        }
        filtered.groupBy { it.country }.toSortedMap()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    init {
        viewModelScope.launch {
            repository.loadCities()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
