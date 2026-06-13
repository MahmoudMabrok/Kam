package tools.mo3ta.kam.ui.revamp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt
import tools.mo3ta.kam.data.City
import tools.mo3ta.kam.domain.CompareResult
import tools.mo3ta.kam.domain.money
import tools.mo3ta.kam.ui.revamp.components.RegionChip
import tools.mo3ta.kam.ui.theme.KamColors
import tools.mo3ta.kam.ui.theme.KamDims
import tools.mo3ta.kam.ui.theme.LocalKamText
import tools.mo3ta.kam.viewmodel.PickerRole
import tools.mo3ta.kam.viewmodel.RevampUiState

@Composable
fun HomeScreen(
    state: RevampUiState,
    onOpenPicker: (PickerRole) -> Unit,
    onSwap: () -> Unit,
    onEditSalary: () -> Unit,
    onEditOffer: () -> Unit,
    onClearOffer: () -> Unit,
    onCompare: () -> Unit,
    onSettings: () -> Unit,
) {
    if (state.origin == null || state.dest == null) {
        Box(Modifier.fillMaxWidth())
        return
    }
    val origin = state.origin!!
    val dest = state.dest!!
    val r = state.result
    val text = LocalKamText.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(KamColors.paper)
            .padding(horizontal = KamDims.screenH)
            .padding(bottom = KamDims.bottomPad),
    ) {
        Spacer(Modifier.height(12.dp))

        // 1. Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Wordmark
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    // halo
                    Box(
                        Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(KamColors.greenSoft),
                    )
                    Box(
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(KamColors.green),
                    )
                }
                Spacer(Modifier.size(8.dp))
                Text("Kam", style = text.wordmark, color = KamColors.ink)
            }
            // Settings button (>=44dp hit target around the 40dp visual)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onSettings),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(KamColors.card),
                    contentAlignment = Alignment.Center,
                ) {
                    GearIcon()
                }
            }
        }

        // 2. Hero
        val hero = buildAnnotatedString {
            append("How far does your salary ")
            withStyle(SpanStyle(color = KamColors.green)) { append("travel?") }
        }
        Text(
            hero,
            style = text.heroH1,
            color = KamColors.ink,
            modifier = Modifier.padding(top = 18.dp),
        )
        Text(
            "Compare living costs and real purchasing power between any two cities.",
            style = text.heroSub,
            color = KamColors.ink2,
            modifier = Modifier.padding(top = 11.dp),
        )

        // 3. Compare card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp)
                .shadow(8.dp, KamDims.cardShapeLg)
                .clip(KamDims.cardShapeLg)
                .background(KamColors.card)
                .padding(7.dp),
        ) {
            // a. FROM selector
            CitySelector(label = "FROM", city = origin) { onOpenPicker(PickerRole.ORIGIN) }

            // b. Salary field
            SalaryField(salary = state.salary, origin = origin, onEditSalary = onEditSalary)

            // c. Divider + swap
            DividerSwap(onSwap = onSwap)

            // d. TO selector
            CitySelector(label = "TO", city = dest) { onOpenPicker(PickerRole.DEST) }

            // e. Offer affordance
            if (state.offerStr.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .drawBehind {
                            val sw = 1.5.dp.toPx()
                            drawRoundRect(
                                color = KamColors.line,
                                topLeft = Offset(sw / 2, sw / 2),
                                size = Size(size.width - sw, size.height - sw),
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                                style = Stroke(
                                    width = sw,
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(6.dp.toPx(), 5.dp.toPx()),
                                        0f,
                                    ),
                                ),
                            )
                        }
                        .clickable(onClick = onEditOffer)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "+ Compare a salary offer in ${dest.name}",
                        style = text.offerAdd,
                        color = KamColors.ink2,
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(KamColors.card2)
                            .border(1.dp, KamColors.line, RoundedCornerShape(16.dp))
                            .clickable(onClick = onEditOffer)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(
                            "Salary offer in ${dest.name}",
                            style = text.cityLabel,
                            color = KamColors.ink3,
                        )
                        Row(
                            modifier = Modifier.padding(top = 5.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            Text(
                                money(state.offer, dest),
                                style = text.ofValue,
                                color = KamColors.ink,
                            )
                            Text(
                                "${dest.currency}/mo",
                                style = text.sfCur,
                                color = KamColors.ink2,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(KamColors.card2)
                            .border(1.dp, KamColors.line, CircleShape)
                            .clickable(onClick = onClearOffer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Canvas(Modifier.size(12.dp)) {
                            val sw = 1.8.dp.toPx()
                            drawLine(
                                KamColors.ink2,
                                Offset(0f, 0f),
                                Offset(size.width, size.height),
                                strokeWidth = sw,
                                cap = StrokeCap.Round,
                            )
                            drawLine(
                                KamColors.ink2,
                                Offset(size.width, 0f),
                                Offset(0f, size.height),
                                strokeWidth = sw,
                                cap = StrokeCap.Round,
                            )
                        }
                    }
                }
            }

            // f. Live strip
            LiveStrip(dest = dest, r = r, offerPresent = state.offer > 0)
        }

        // 4. CTA — gated on FX rates being loaded so Results never computes
        //    with a 1:1 currency fallback.
        val ready = r != null && r.ratesReady
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(KamColors.ink.copy(alpha = if (ready) 1f else 0.45f))
                .clickable(enabled = ready, onClick = onCompare)
                .padding(18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (ready) "See full comparison" else "Loading rates…",
                    style = text.cta,
                    color = KamColors.paper,
                )
                if (ready) {
                    Canvas(Modifier.size(18.dp)) {
                        arrowRight(KamColors.paper, 2.dp.toPx())
                    }
                }
            }
        }
    }
}

@Composable
private fun CitySelector(label: String, city: City, onTap: () -> Unit) {
    val text = LocalKamText.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onTap)
            .padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
        Text(label, style = text.cityLabel, color = KamColors.ink3)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RegionChip(cc = city.countryCode)
            Column(Modifier.weight(1f)) {
                Text(city.name, style = text.cityName, color = KamColors.ink, maxLines = 1)
                Text(
                    city.country,
                    style = text.cityCountry,
                    color = KamColors.ink2,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
            Canvas(Modifier.size(width = 9.dp, height = 16.dp)) {
                val sw = 1.8.dp.toPx()
                drawLine(
                    KamColors.ink3,
                    Offset(0f, 0f),
                    Offset(size.width, size.height / 2),
                    strokeWidth = sw,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    KamColors.ink3,
                    Offset(size.width, size.height / 2),
                    Offset(0f, size.height),
                    strokeWidth = sw,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun SalaryField(salary: Double, origin: City, onEditSalary: () -> Unit) {
    val text = LocalKamText.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(KamDims.fieldShape)
                .background(KamColors.salaryGradient)
                .clickable(onClick = onEditSalary)
                .padding(horizontal = 17.dp, vertical = 15.dp),
        ) {
            Column {
                Text(
                    "YOUR MONTHLY SALARY",
                    style = text.sfLabel,
                    color = KamColors.onDark.copy(alpha = 0.55f),
                )
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(money(salary, origin), style = text.sfAmount, color = KamColors.onDark)
                    Text(
                        "${origin.currency}/mo",
                        style = text.sfCur,
                        color = KamColors.onDark.copy(alpha = 0.6f),
                    )
                }
            }
            // Edit pill, top-end
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(20.dp))
                    .background(KamColors.greenSoft)
                    .padding(horizontal = 11.dp, vertical = 5.dp),
            ) {
                Text(
                    "Edit",
                    style = text.offerAdd.copy(fontSize = 12.5.sp),
                    color = KamColors.green,
                )
            }
        }
    }
}

@Composable
private fun DividerSwap(onSwap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        // divider line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(KamColors.line),
        )
        // swap button at the right end
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(42.dp)
                .shadow(4.dp, RoundedCornerShape(13.dp))
                .clip(RoundedCornerShape(13.dp))
                .background(KamColors.card)
                .border(1.dp, KamColors.line, RoundedCornerShape(13.dp))
                .clickable(onClick = onSwap),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.size(20.dp)) {
                val sw = 1.8.dp.toPx()
                val w = size.width
                val h = size.height
                // left arrow: down at x=0.3
                val lx = w * 0.3f
                drawLine(KamColors.ink, Offset(lx, h * 0.15f), Offset(lx, h * 0.7f), sw, StrokeCap.Round)
                drawLine(KamColors.ink, Offset(lx, h * 0.7f), Offset(lx - w * 0.15f, h * 0.55f), sw, StrokeCap.Round)
                drawLine(KamColors.ink, Offset(lx, h * 0.7f), Offset(lx + w * 0.15f, h * 0.55f), sw, StrokeCap.Round)
                // right arrow: up at x=0.7
                val rx = w * 0.7f
                drawLine(KamColors.ink, Offset(rx, h * 0.85f), Offset(rx, h * 0.3f), sw, StrokeCap.Round)
                drawLine(KamColors.ink, Offset(rx, h * 0.3f), Offset(rx - w * 0.15f, h * 0.45f), sw, StrokeCap.Round)
                drawLine(KamColors.ink, Offset(rx, h * 0.3f), Offset(rx + w * 0.15f, h * 0.45f), sw, StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun LiveStrip(dest: City, r: CompareResult?, offerPresent: Boolean) {
    val text = LocalKamText.current
    val modBase = Modifier
        .fillMaxWidth()
        .padding(7.dp)

    if (r == null || !r.ratesReady) {
        Box(
            modifier = modBase
                .clip(RoundedCornerShape(18.dp))
                .background(KamColors.card2)
                .padding(horizontal = 16.dp, vertical = 13.dp),
        ) {
            Text("Calculating…", style = text.liveStrip, color = KamColors.ink2)
        }
        return
    }

    val useOffer = offerPresent && r.offer != null
    val positive = if (useOffer) r.offer!!.vsBreakevenPct >= 0 else r.costDeltaPct < 0
    val pct = abs((if (useOffer) r.offer!!.vsBreakevenPct else r.costDeltaPct).roundToInt())
    val arrow = if (useOffer) {
        if (positive) "↑" else "↓"
    } else {
        if (positive) "↓" else "↑"
    }
    val accentColor = if (positive) KamColors.green else KamColors.terra
    val annotated = buildAnnotatedString {
        if (useOffer) {
            append("Offer is ")
            withStyle(SpanStyle(color = accentColor)) {
                append("$pct% ${if (positive) "above" else "below"}")
            }
            append(" your breakeven")
        } else {
            append("${dest.name} is ")
            withStyle(SpanStyle(color = accentColor)) {
                append("$pct% ${if (positive) "cheaper" else "pricier"}")
            }
        }
    }
    val accent = accentColor
    val bg = if (positive) KamColors.greenSoft else KamColors.terraSoft

    Row(
        modifier = modBase
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(arrow, style = text.liveStrip.copy(fontSize = 18.sp), color = accent)
        Text(annotated, style = text.liveStrip, color = KamColors.ink2)
    }
}

private fun DrawScope.arrowRight(color: Color, sw: Float) {
    val w = size.width
    val h = size.height
    val midY = h / 2
    drawLine(color, Offset(w * 0.15f, midY), Offset(w * 0.82f, midY), sw, StrokeCap.Round)
    drawLine(color, Offset(w * 0.82f, midY), Offset(w * 0.58f, midY - h * 0.24f), sw, StrokeCap.Round)
    drawLine(color, Offset(w * 0.82f, midY), Offset(w * 0.58f, midY + h * 0.24f), sw, StrokeCap.Round)
}

@Composable
private fun GearIcon() {
    Canvas(Modifier.size(20.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val sw = 1.7.dp.toPx()
        val cr = 2.4.dp.toPx()
        drawCircle(KamColors.ink2, radius = cr, center = Offset(cx, cy), style = Stroke(width = sw))
        val rOuter = size.width * 0.42f
        val rInner = size.width * 0.30f
        for (i in 0 until 8) {
            val ang = (i * 45.0) * (kotlin.math.PI / 180.0)
            val dx = kotlin.math.cos(ang).toFloat()
            val dy = kotlin.math.sin(ang).toFloat()
            drawLine(
                KamColors.ink2,
                Offset(cx + dx * rInner, cy + dy * rInner),
                Offset(cx + dx * rOuter, cy + dy * rOuter),
                strokeWidth = sw,
                cap = StrokeCap.Round,
            )
        }
    }
}
