package tools.mo3ta.kam.ui.revamp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tools.mo3ta.kam.domain.CurrencySymbols
import tools.mo3ta.kam.domain.groupThousands
import tools.mo3ta.kam.ui.revamp.components.KamBottomSheet
import tools.mo3ta.kam.ui.revamp.components.Keypad
import tools.mo3ta.kam.ui.revamp.components.RegionChip
import tools.mo3ta.kam.ui.theme.KamColors
import tools.mo3ta.kam.ui.theme.LocalKamText
import tools.mo3ta.kam.viewmodel.KeypadTarget
import tools.mo3ta.kam.viewmodel.PickerRole
import tools.mo3ta.kam.viewmodel.RevampUiState
import kotlin.math.roundToInt

@Composable
fun PickerSheet(
    state: RevampUiState,
    onSelect: (String) -> Unit,   // city id
    onClose: () -> Unit,
) {
    val text = LocalKamText.current
    val role = state.picker
    val currentId = if (role == PickerRole.ORIGIN) state.origin?.id else state.dest?.id
    val title = if (role == PickerRole.ORIGIN) "Where do you live now?" else "Compare with…"

    var query by remember { mutableStateOf("") }
    LaunchedEffect(state.picker) { query = "" }

    KamBottomSheet(
        visible = state.picker != null,
        onDismiss = onClose,
        title = title,
        fillHeight = true,
    ) {
        Column(Modifier.fillMaxWidth()) {
            // Search field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(KamColors.card)
                    .border(1.dp, KamColors.line, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Canvas(Modifier.size(17.dp)) {
                    val w = size.width
                    val h = size.height
                    val sw = 1.7.dp.toPx()
                    val r = w * 0.32f
                    val cx = w * 0.41f
                    val cy = h * 0.41f
                    drawCircle(
                        color = KamColors.ink3,
                        radius = r,
                        center = Offset(cx, cy),
                        style = Stroke(width = sw),
                    )
                    drawLine(
                        color = KamColors.ink3,
                        start = Offset(cx + r * 0.7f, cy + r * 0.7f),
                        end = Offset(w * 0.92f, h * 0.92f),
                        strokeWidth = sw,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = text.searchInput.copy(color = KamColors.ink),
                    cursorBrush = SolidColor(KamColors.green),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                "Search city or country",
                                style = text.searchInput,
                                color = KamColors.ink3,
                            )
                        }
                        innerTextField()
                    },
                )
            }

            val filtered = state.cities.filter {
                (it.name + " " + it.country).lowercase().contains(query.lowercase())
            }

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        "No cities match “$query”.",
                        style = text.searchInput.copy(textAlign = TextAlign.Center),
                        color = KamColors.ink3,
                        modifier = Modifier.padding(30.dp),
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 10.dp),
                ) {
                    items(filtered) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelect(city.id) }
                                .padding(horizontal = 8.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(13.dp),
                        ) {
                            RegionChip(city.countryCode)
                            Column(Modifier.weight(1f)) {
                                Text(city.name, style = text.clrName, color = KamColors.ink)
                                Text(city.country, style = text.clrCountry, color = KamColors.ink2)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(KamColors.card2)
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    city.baseIndex.roundToInt().toString(),
                                    style = text.clrIndex,
                                    color = KamColors.ink3,
                                )
                            }
                            if (city.id == currentId) {
                                Canvas(Modifier.size(16.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    val path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.125f, h * 0.5f)
                                        lineTo(w * 0.375f, h * 0.75f)
                                        lineTo(w * 0.875f, h * 0.1875f)
                                    }
                                    drawPath(
                                        path,
                                        KamColors.green,
                                        style = Stroke(
                                            width = 2.2.dp.toPx(),
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                            join = androidx.compose.ui.graphics.StrokeJoin.Round,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeypadSheet(
    state: RevampUiState,
    onKey: (String) -> Unit,      // new full value string (already-edited)
    onClose: () -> Unit,
) {
    val text = LocalKamText.current
    val target = state.keypad
    val city = if (target == KeypadTarget.OFFER) state.dest else state.origin

    if (city == null) {
        KamBottomSheet(visible = false, onDismiss = onClose, title = "") {}
        return
    }

    val currentValue = if (target == KeypadTarget.OFFER) state.offerStr else state.salaryStr
    val title = if (target == KeypadTarget.OFFER) {
        "Salary offer in ${state.dest?.name}"
    } else {
        "Your monthly salary"
    }

    KamBottomSheet(
        visible = state.keypad != null,
        onDismiss = onClose,
        title = title,
        fillHeight = false,
    ) {
        Column(Modifier.fillMaxWidth()) {
            // Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    CurrencySymbols.of(city.currency),
                    style = text.kpSym,
                    color = KamColors.ink2,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    formatDisplay(currentValue),
                    style = text.kpNum,
                    color = KamColors.ink,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    city.currency,
                    style = text.kpCur,
                    color = KamColors.ink3,
                )
            }

            Keypad(onKey = { k -> onKey(applyKey(currentValue, k)) })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(KamColors.ink)
                    .clickable { onClose() }
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Done", style = text.cta, color = KamColors.paper)
            }
        }
    }
}

/** Format the typed value with thousands separators, preserving a trailing "." or decimals. */
private fun formatDisplay(value: String): String {
    if (value.isBlank()) return "0"
    val dot = value.indexOf('.')
    if (dot < 0) {
        return groupThousands(value.toLongOrNull() ?: 0)
    }
    val intPart = value.substring(0, dot)
    val frac = value.substring(dot + 1)
    val grouped = groupThousands(intPart.toLongOrNull() ?: 0)
    return "$grouped.$frac"
}

/** Pure keypad edit replicating app.jsx KeypadSheet.handle. */
private fun applyKey(value: String, key: String): String {
    val result = when (key) {
        "del" -> {
            val s = value.dropLast(1)
            if (s.isEmpty()) "0" else s
        }
        "." -> if (value.contains(".")) value else value + "."
        else -> (if (value == "0") "" else value) + key
    }
    return if (result.replace(".", "").length <= 9) result else value
}
