package tools.mo3ta.kam.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import tools.mo3ta.kam.data.City

private fun countryFlag(countryCode: String): String {
    if (countryCode.length != 2) return "🏳️"
    val first = 0x1F1E6 - 'A'.code + countryCode[0].uppercaseChar().code
    val second = 0x1F1E6 - 'A'.code + countryCode[1].uppercaseChar().code
    return String(intArrayOf(first, second), 0, 2)
}

@Composable
fun CityPickerDropdown(
    label: String,
    selectedCity: City?,
    cities: List<City>,
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedCity?.let {
                "${countryFlag(it.countryCode)} ${it.name}, ${it.country}"
            } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            enabled = false,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false }
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .heightIn(max = 350.dp)
                ) {
                    Column {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search city...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )

                        val filtered = cities.filter {
                            it.name.contains(searchText, ignoreCase = true) ||
                                it.country.contains(searchText, ignoreCase = true)
                        }

                        LazyColumn(modifier = Modifier.heightIn(max = 280.dp)) {
                            items(filtered, key = { it.id }) { city ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onCitySelected(city)
                                            expanded = false
                                            searchText = ""
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = countryFlag(city.countryCode),
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = city.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = city.country,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    CostIndexBadge(index = city.baseIndex)
                                }
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
