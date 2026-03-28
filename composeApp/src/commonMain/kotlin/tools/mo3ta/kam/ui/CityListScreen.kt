package tools.mo3ta.kam.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tools.mo3ta.kam.data.City
import tools.mo3ta.kam.data.Indices
import tools.mo3ta.kam.data.Summary
import tools.mo3ta.kam.ui.components.CostIndexBadge
import tools.mo3ta.kam.viewmodel.CityListViewModel
import tools.mo3ta.kam.analytics.logCityCardExpanded

private fun countryFlag(countryCode: String): String {
    if (countryCode.length != 2) return "🏳️"
    val first = 0x1F1E6 - 'A'.code + countryCode[0].uppercaseChar().code
    val second = 0x1F1E6 - 'A'.code + countryCode[1].uppercaseChar().code
    return String(intArrayOf(first, second), 0, 2)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityListScreen(viewModel: CityListViewModel) {
    val strings = LocalKamStrings.current
    val groupedCities by viewModel.groupedCities.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        // focusRequester.requestFocus() // Optional: auto-focus on screen entry
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Text(
                    text = strings.citiesTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = strings.citiesCountTemplate(groupedCities.values.sumOf { it.size }, groupedCities.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text(strings.searchPlaceholder) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            groupedCities.forEach { (country, cities) ->
                val firstCity = cities.first()
                stickyHeader(key = "header_$country") {
                    CountryHeader(
                        country = country,
                        countryCode = firstCity.countryCode,
                        currency = firstCity.currency,
                        cityCount = cities.size,
                        summary = firstCity.summary
                    )
                }

                items(cities, key = { it.id }) { city ->
                    CityCard(city = city)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Country sticky header — shows summary info drawn from the country's first city
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CountryHeader(
    country: String,
    countryCode: String,
    currency: String,
    cityCount: Int,
    summary: tools.mo3ta.kam.data.Summary
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.97f))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${countryFlag(countryCode)}  $country",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${cityCount} ${LocalKamStrings.current.tabCities}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Summary chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryChip(
                emoji = "👤",
                label = LocalKamStrings.current.singleLabel,
                value = "${summary.singleMonthly.toInt()} $currency/mo"
            )
            SummaryChip(
                emoji = "👨‍👩‍👧",
                label = LocalKamStrings.current.familyLabel,
                value = "${summary.familyMonthly.toInt()} $currency/mo"
            )
            summary.avgSalary?.let {
                SummaryChip(
                    emoji = "💼",
                    label = LocalKamStrings.current.avgSalaryLabel,
                    value = "${it.toInt()} $currency"
                )
            }
        }
    }
}

@Composable
private fun SummaryChip(emoji: String, label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 13.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 10.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// City card — tap to expand and see indices
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CityCard(city: City) {
    val isExpensive = city.baseIndex >= 80
    val isCheap = city.baseIndex < 30
    var expanded by remember { mutableStateOf(false) }

    val highlightColor by animateColorAsState(
        targetValue = when {
            isExpensive -> Color(0xFFB71C1C).copy(alpha = 0.08f)
            isCheap -> Color(0xFF1B5E20).copy(alpha = 0.08f)
            else -> Color.Transparent
        },
        animationSpec = tween(500)
    )
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(250)
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(highlightColor)
        ) {
            // Main row — tappable
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                        if (expanded) {
                            logCityCardExpanded(city.name, city.country)
                        }
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = city.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isExpensive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🔥", fontSize = 14.sp)
                        }
                        if (isCheap) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("💚", fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = city.currency,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                CostIndexBadge(index = city.baseIndex)
                Spacer(modifier = Modifier.width(8.dp))
                // Chevron
                Text(
                    text = "▾",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }

            // Expandable indices panel
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text(
                        text = LocalKamStrings.current.costIndicesTitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    IndicesGrid(indices = city.indices)
                }
            }
        }
    }
}

@Composable
private fun IndicesGrid(indices: Indices) {
    val strings = LocalKamStrings.current
    val rows = listOf(
        Triple("🛒", strings.indexCOL, indices.costOfLiving),
        Triple("🏠", strings.indexRent, indices.rent),
        Triple("🥦", strings.indexGroceries, indices.groceries),
        Triple("🍽️", strings.indexRestaurant, indices.restaurant),
        Triple("💪", strings.indexPurchasingPower, indices.purchasingPower)
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { (emoji, label, value) ->
            IndexRow(emoji = emoji, label = label, value = value)
        }
    }
}

@Composable
private fun IndexRow(emoji: String, label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 15.sp, modifier = Modifier.width(26.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        // Mini bar
        val fraction = (value.toFloat() / 120f).coerceIn(0f, 1f)
        val barColor = when {
            value < 30 -> Color(0xFF66BB6A)
            value < 60 -> Color(0xFFFFCA28)
            value < 90 -> Color(0xFFFF7043)
            else -> Color(0xFFEF5350)
        }
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "%.1f".format(value),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = barColor
        )
    }
}
