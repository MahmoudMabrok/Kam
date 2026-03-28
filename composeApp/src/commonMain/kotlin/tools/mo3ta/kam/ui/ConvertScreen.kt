package tools.mo3ta.kam.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import tools.mo3ta.kam.ui.components.CityPickerDropdown
import tools.mo3ta.kam.viewmodel.ConversionResult
import tools.mo3ta.kam.viewmodel.ConvertViewModel
import tools.mo3ta.kam.viewmodel.LifestyleLevel

@Composable
fun ConvertScreen(viewModel: ConvertViewModel) {
    val strings = LocalKamStrings.current
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
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
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = strings.convertTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = strings.convertDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Inputs ───────────────────────────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CityPickerDropdown(
                label = strings.currentCityLabel,
                selectedCity = state.fromCity,
                cities = state.cities,
                onCitySelected = viewModel::onFromCitySelected
            )

            // Swap button
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = viewModel::swapCities,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("⇅", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            CityPickerDropdown(
                label = strings.destinationCityLabel,
                selectedCity = state.toCity,
                cities = state.cities,
                onCitySelected = viewModel::onToCitySelected
            )

            OutlinedTextField(
                value = state.salary,
                onValueChange = viewModel::onSalaryChanged,
                label = {
                    Text(strings.salaryLabel(state.fromCity?.currency ?: "origin currency"))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = state.result != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                state.result?.let { result ->
                    ResultPanel(
                        result = result,
                        salary = state.salary.toDoubleOrNull() ?: 0.0,
                        fromCityName = state.fromCity?.name ?: "",
                        toCityName = state.toCity?.name ?: "",
                        fromCurrency = state.fromCity?.currency ?: "",
                        toCurrency = state.toCity?.currency ?: ""
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top-level result panel composed of two scenario cards + a breakeven summary
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultPanel(
    result: ConversionResult,
    salary: Double,
    fromCityName: String,
    toCityName: String,
    fromCurrency: String,
    toCurrency: String
) {
    val strings = LocalKamStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // ── Scenario A: If you move with the same salary ─────────────────────
        ScenarioCard(
            label = strings.scenarioASameSalary,
            sublabel = strings.scenarioASublabel(salary.toLong(), fromCurrency, toCityName),
            accentColor = if (result.purchasingPowerChangePct < 0)
                Color(0xFFEF5350) else Color(0xFF66BB6A),
            content = {
                PurchasingPowerRow(
                    leftLabel = "In $fromCityName",
                    leftValue = "%.1f units".format(result.normalizedFrom),
                    leftLifestyle = result.fromLifestyle,
                    rightLabel = "Same salary in $toCityName",
                    rightValue = "%.1f units".format(result.normalizedIfMoved),
                    rightLifestyle = result.movedLifestyle,
                    changePct = result.purchasingPowerChangePct
                )
                Spacer(modifier = Modifier.height(8.dp))
                val sign = if (result.purchasingPowerChangePct >= 0) "+" else ""
                val color = if (result.purchasingPowerChangePct >= 0)
                    Color(0xFF66BB6A) else Color(0xFFEF5350)
                val verdict = if (result.purchasingPowerChangePct >= 0)
                    strings.scenarioAVerdictPositive
                else
                    strings.scenarioAVerdictNegative
                VerdictBox(
                    color = color,
                    text = "$verdict  ($sign${"%.1f".format(result.purchasingPowerChangePct)}%)"
                )
            }
        )

        // ── Scenario B: Breakeven — salary you'd need ──────────────────────
        ScenarioCard(
            label = strings.scenarioBMaintainLifestyle,
            sublabel = strings.scenarioBSublabel(toCityName),
            accentColor = MaterialTheme.colorScheme.primary,
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${salary.toLong()} $fromCurrency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "in $fromCityName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "=",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = strings.scenarioBEquivalent(result.equivalentSalary.toLong(), toCurrency),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = strings.scenarioBNeededIn(toCityName),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Cost of living gap
                val sign = if (result.costOfLivingGapPct >= 0) "+" else ""
                val isCheaper = result.costOfLivingGapPct < 0
                VerdictBox(
                    color = if (isCheaper) Color(0xFF66BB6A) else Color(0xFFFF7043),
                    text = if (isCheaper)
                        strings.costOfLivingGapCheaper(toCityName, result.costOfLivingGapPct)
                    else
                        strings.costOfLivingGapExpensive(toCityName, result.costOfLivingGapPct)
                )
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable card wrapper for each scenario
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ScenarioCard(
    label: String,
    sublabel: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 20.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = sublabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 12.dp)
            )
            content()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Side-by-side purchasing power comparison
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PurchasingPowerRow(
    leftLabel: String,
    leftValue: String,
    leftLifestyle: LifestyleLevel,
    rightLabel: String,
    rightValue: String,
    rightLifestyle: LifestyleLevel,
    changePct: Double
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        LifestyleBlock(
            cityLabel = leftLabel,
            powerValue = leftValue,
            level = leftLifestyle,
            isLeft = true
        )

        // Centre arrow with delta
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val arrowColor = if (changePct >= 0) Color(0xFF66BB6A) else Color(0xFFEF5350)
            val arrow = if (changePct >= 0) "↗" else "↘"
            Text(arrow, fontSize = 22.sp, color = arrowColor)
            val sign = if (changePct >= 0) "+" else ""
            Text(
                text = "$sign${"%.1f".format(changePct)}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = arrowColor
            )
        }

        LifestyleBlock(
            cityLabel = rightLabel,
            powerValue = rightValue,
            level = rightLifestyle,
            isLeft = false
        )
    }
}

@Composable
private fun LifestyleBlock(
    cityLabel: String,
    powerValue: String,
    level: LifestyleLevel,
    isLeft: Boolean
) {
    val (bg, fg) = when (level) {
        LifestyleLevel.BASIC -> Color(0xFF37474F) to Color(0xFFB0BEC5)
        LifestyleLevel.MIDDLE -> Color(0xFF1565C0).copy(alpha = 0.6f) to Color(0xFF90CAF9)
        LifestyleLevel.COMFORTABLE -> Color(0xFF2E7D32).copy(alpha = 0.6f) to Color(0xFFA5D6A7)
        LifestyleLevel.LUXURY -> Color(0xFF6A1B9A).copy(alpha = 0.6f) to Color(0xFFCE93D8)
    }

    Column(
        horizontalAlignment = if (isLeft) Alignment.Start else Alignment.End,
        modifier = Modifier.width(130.dp)
    ) {
        Text(
            text = cityLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = if (isLeft) TextAlign.Start else TextAlign.End
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(bg)
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(level.emoji, fontSize = 20.sp)
                Text(
                    text = level.label,
                    color = fg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = powerValue,
                    color = fg.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Verdict banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun VerdictBox(color: Color, text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            textAlign = TextAlign.Start
        )
    }
}
