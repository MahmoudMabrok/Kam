package tools.mo3ta.kam.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tools.mo3ta.kam.data.City
import tools.mo3ta.kam.ui.components.CityPickerDropdown
import tools.mo3ta.kam.viewmodel.CompareResult
import tools.mo3ta.kam.viewmodel.CompareViewModel

@Composable
fun CompareScreen(viewModel: CompareViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "⚖️ Compare Salaries",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Normalize salaries against city cost indices",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SalaryInputColumn(
                modifier = Modifier.weight(1f),
                title = "Option A",
                titleColor = MaterialTheme.colorScheme.primary,
                city = state.city1,
                salary = state.salary1,
                cities = state.cities,
                onCitySelected = viewModel::onCity1Selected,
                onSalaryChanged = viewModel::onSalary1Changed,
                isWinner = state.result?.winner == 1
            )
            SalaryInputColumn(
                modifier = Modifier.weight(1f),
                title = "Option B",
                titleColor = MaterialTheme.colorScheme.secondary,
                city = state.city2,
                salary = state.salary2,
                cities = state.cities,
                onCitySelected = viewModel::onCity2Selected,
                onSalaryChanged = viewModel::onSalary2Changed,
                isWinner = state.result?.winner == 2
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = state.result != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            state.result?.let { result ->
                CompareResultCard(
                    result = result,
                    city1Name = state.city1?.name ?: "A",
                    city2Name = state.city2?.name ?: "B",
                    salary1 = state.salary1.toDoubleOrNull() ?: 0.0,
                    salary2 = state.salary2.toDoubleOrNull() ?: 0.0,
                    currency1 = state.city1?.currency ?: "",
                    currency2 = state.city2?.currency ?: ""
                )
            }
        }
    }
}

@Composable
private fun SalaryInputColumn(
    modifier: Modifier = Modifier,
    title: String,
    titleColor: Color,
    city: City?,
    salary: String,
    cities: List<City>,
    onCitySelected: (City) -> Unit,
    onSalaryChanged: (String) -> Unit,
    isWinner: Boolean
) {
    val borderColor by animateColorAsState(
        targetValue = if (isWinner) Color(0xFF66BB6A) else Color.Transparent,
        animationSpec = tween(500)
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isWinner) 6.dp else 1.dp),
        border = if (isWinner) androidx.compose.foundation.BorderStroke(2.dp, borderColor) else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                if (isWinner) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("🏆", fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            CityPickerDropdown(
                label = "City",
                selectedCity = city,
                cities = cities,
                onCitySelected = onCitySelected
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = salary,
                onValueChange = onSalaryChanged,
                label = { Text("Salary") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CompareResultCard(
    result: CompareResult,
    city1Name: String,
    city2Name: String,
    salary1: Double,
    salary2: Double,
    currency1: String,
    currency2: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "📊 Purchasing Power Comparison",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                NormBar(
                    label = city1Name,
                    salary = salary1,
                    currency = currency1,
                    normalized = result.normalized1,
                    maxVal = maxOf(result.normalized1, result.normalized2),
                    isWinner = result.winner == 1,
                    color = MaterialTheme.colorScheme.primary
                )
                NormBar(
                    label = city2Name,
                    salary = salary2,
                    currency = currency2,
                    normalized = result.normalized2,
                    maxVal = maxOf(result.normalized1, result.normalized2),
                    isWinner = result.winner == 2,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))

            val verdict = when (result.winner) {
                1 -> "🏆 $city1Name wins with ${"%.1f".format(result.percentageBetter)}% more purchasing power"
                2 -> "🏆 $city2Name wins with ${"%.1f".format(result.percentageBetter)}% more purchasing power"
                else -> "🤝 Both salaries have equal purchasing power"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = verdict,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun NormBar(
    label: String,
    salary: Double,
    currency: String,
    normalized: Double,
    maxVal: Double,
    isWinner: Boolean,
    color: Color
) {
    val fraction = if (maxVal == 0.0) 0f else (normalized / maxVal).toFloat().coerceIn(0f, 1f)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(130.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        if (isWinner) Text("🏆", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(fraction)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(color.copy(alpha = 0.5f), color)
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${salary.toLong()} $currency",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Power: ${"%.1f".format(normalized)}",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
