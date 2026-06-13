package tools.mo3ta.kam.ui.revamp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt
import tools.mo3ta.kam.domain.fxBetween
import tools.mo3ta.kam.domain.money
import tools.mo3ta.kam.ui.revamp.components.Breakdown
import tools.mo3ta.kam.ui.revamp.components.PowerBar
import tools.mo3ta.kam.ui.revamp.components.RegionChip
import tools.mo3ta.kam.ui.revamp.components.TierMeter
import tools.mo3ta.kam.ui.theme.KamColors
import tools.mo3ta.kam.ui.theme.KamDims
import tools.mo3ta.kam.ui.theme.LocalKamText
import tools.mo3ta.kam.viewmodel.RevampUiState

@Composable
fun ResultScreen(
    state: RevampUiState,
    onBack: () -> Unit,
) {
    val origin = state.origin ?: return
    val dest = state.dest ?: return
    val r = state.result ?: return
    val text = LocalKamText.current

    Column(
        Modifier
            .fillMaxWidth()
            .background(KamColors.paper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = KamDims.bottomPad),
    ) {
        // 1. Top bar
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onBack)
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RegionChip(cc = origin.countryCode, size = 22.dp)
                Text(origin.name, style = text.routeMini, color = KamColors.ink)
                Text("→", style = text.routeMini, color = KamColors.ink3)
                RegionChip(cc = dest.countryCode, size = 22.dp)
                Text(dest.name, style = text.routeMini, color = KamColors.ink)
            }
        }

        // 2. Verdict headline
        val deltaN = abs(r.costDeltaPct.roundToInt())
        val verdictColor = if (r.cheaper) KamColors.green else KamColors.terra
        val verdict = buildAnnotatedString {
            withStyle(SpanStyle(color = KamColors.ink)) { append("${dest.name} is ") }
            withStyle(SpanStyle(color = verdictColor)) {
                append("$deltaN% ${if (r.cheaper) "cheaper" else "more expensive"}")
            }
            withStyle(SpanStyle(color = KamColors.ink)) { append(" than ${origin.name}.") }
        }
        Text(verdict, style = text.verdict, modifier = Modifier.padding(top = 18.dp))

        // 3. Hero breakeven card
        HeroCard(state, origin, dest, r)

        // 4. Offer card (optional)
        val offer = r.offer
        if (offer != null) {
            RevampCard {
                val vs = offer.vsBreakevenPct
                val vsRound = abs(vs.roundToInt())
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("YOUR OFFER", style = text.cardHead, color = KamColors.ink3)
                    CardBadge(
                        text = "${if (vs >= 0) "+" else "−"}$vsRound% vs breakeven",
                        positive = vs >= 0,
                    )
                }
                Row(
                    Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(money(state.offer, dest), style = text.ocAmount, color = KamColors.ink)
                    Text("/mo", style = text.ocDesc, color = KamColors.ink3, modifier = Modifier.padding(start = 2.dp))
                }
                val ocDesc = buildAnnotatedString {
                    withStyle(SpanStyle(color = KamColors.ink2)) { append("Worth ") }
                    bold(money(offer.equivalentHomeLocal, origin))
                    withStyle(SpanStyle(color = KamColors.ink2)) {
                        append(" in ${origin.name} terms — your lifestyle would ${if (vs >= 0) "improve" else "decline"} by about ")
                    }
                    bold("$vsRound%")
                    withStyle(SpanStyle(color = KamColors.ink2)) { append(".") }
                }
                Text(ocDesc, style = text.ocDesc, modifier = Modifier.padding(top = 10.dp))
            }
        }

        // 5. Purchasing power card
        RevampCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("PURCHASING POWER", style = text.cardHead, color = KamColors.ink3)
                CardBadge(
                    text = if (r.powerUp) "goes further" else "goes less far",
                    positive = r.powerUp,
                )
            }
            val powerColor = if (r.powerUp) KamColors.green else KamColors.terra
            val pctStr = "${if (r.powerUp) "+" else ""}${r.powerPct.roundToInt()}%"
            val headline = buildAnnotatedString {
                withStyle(SpanStyle(color = KamColors.ink)) { append("Your money goes ") }
                withStyle(SpanStyle(color = powerColor, fontWeight = FontWeight.Bold)) { append(pctStr) }
                withStyle(SpanStyle(color = KamColors.ink)) {
                    append(" ${if (r.powerUp) "further" else "less far"}")
                }
            }
            Text(
                headline,
                style = text.powerHeadline,
                modifier = Modifier.padding(top = 14.dp, bottom = 16.dp),
            )
            PowerBar(pct = r.powerPct)
        }

        // 6. Lifestyle level card
        RevampCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("LIFESTYLE LEVEL", style = text.cardHead, color = KamColors.ink3)
                if (r.offer != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // hollow ring (now)
                        RingDot()
                        Text(" now", style = text.legend, color = KamColors.ink3)
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .size(9.dp)
                                .clip(RoundedCornerShape(50))
                                .background(KamColors.ink),
                        )
                        Text(" with offer", style = text.legend, color = KamColors.ink3)
                    }
                }
            }
            val t = r.offer?.tier ?: r.tier
            val pos = r.offer?.tierPos ?: r.tierPos
            Column(Modifier.padding(top = 13.dp, bottom = 16.dp)) {
                Text(t.label, style = text.tierName, color = KamColors.tier[t.index])
                Text(
                    t.blurb,
                    style = text.tierBlurb,
                    color = KamColors.ink2,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }
            TierMeter(
                tierIndex = t.index,
                pos = pos,
                ghostPos = if (r.offer != null) r.homeTierPos else null,
            )
        }

        // 7. Cost breakdown card
        RevampCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("COST BREAKDOWN", style = text.cardHead, color = KamColors.ink3)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(9.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(KamColors.ink3.copy(alpha = 0.55f)),
                    )
                    Text(" ${origin.name}", style = text.legend, color = KamColors.ink3)
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .size(9.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(KamColors.green),
                    )
                    Text(" ${dest.name}", style = text.legend, color = KamColors.ink3)
                }
            }
            Breakdown(origin = origin, dest = dest)
        }

        // 8. Footnote
        val fx = fxBetween(origin, dest, state.rates)
        val footNote = buildString {
            append("Indexed to New York = 100. ")
            if (fx != null) {
                val fx2 = (fx * 100).roundToInt() / 100.0
                append("Currency converted at 1 ${origin.currency} ≈ $fx2 ${dest.currency}. ")
            }
            append("Estimates for guidance only.")
        }
        Text(
            footNote,
            style = text.footNote,
            color = KamColors.ink3,
            modifier = Modifier.padding(top = 18.dp),
        )
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    Box(
        Modifier
            .size(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onBack,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .shadow(2.dp, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(KamColors.card),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.size(11.dp, 18.dp)) {
                val w = size.width
                val h = size.height
                val stroke = Stroke(width = 2.2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.82f, h * 0.06f)
                    lineTo(w * 0.18f, h * 0.5f)
                    lineTo(w * 0.82f, h * 0.94f)
                }
                drawPath(path, KamColors.ink, style = stroke)
            }
        }
    }
}

@Composable
private fun RingDot() {
    Box(
        Modifier
            .size(6.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.Transparent),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(6.dp)) {
            drawCircle(
                color = KamColors.ink2,
                radius = size.minDimension / 2 - 1.dp.toPx() / 2,
                style = Stroke(width = 1.5.dp.toPx()),
            )
        }
    }
}

@Composable
private fun RevampCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .shadow(1.dp, KamDims.cardShape)
            .clip(KamDims.cardShape)
            .background(KamColors.card)
            .padding(18.dp),
        content = content,
    )
}

@Composable
private fun CardBadge(text: String, positive: Boolean) {
    val styles = LocalKamText.current
    val bg = if (positive) KamColors.greenSoft else KamColors.terraSoft
    val fg = if (positive) KamColors.green else KamColors.terra
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 4.dp),
    ) {
        Text(text, style = styles.cardBadge, color = fg)
    }
}

@Composable
private fun HeroCard(
    state: RevampUiState,
    origin: tools.mo3ta.kam.data.City,
    dest: tools.mo3ta.kam.data.City,
    r: tools.mo3ta.kam.domain.CompareResult,
) {
    val text = LocalKamText.current
    val onDark = KamColors.onDark
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .shadow(8.dp, KamDims.cardShape)
            .clip(KamDims.cardShape)
            .background(KamColors.heroGradient)
            .padding(22.dp),
    ) {
        Text("BREAKEVEN SALARY", style = text.hcTag, color = onDark.copy(alpha = 0.5f))
        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(money(r.breakevenLocal, dest), style = text.hcAmount, color = onDark)
            Text("/mo", style = text.hcPer, color = onDark.copy(alpha = 0.55f))
        }
        val desc = buildAnnotatedString {
            withStyle(SpanStyle(color = onDark.copy(alpha = 0.78f))) {
                append("earn this in ${dest.name} to keep the lifestyle your ")
            }
            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.SemiBold)) {
                append(money(state.salary, origin))
            }
            withStyle(SpanStyle(color = onDark.copy(alpha = 0.78f))) {
                append(" buys in ${origin.name}.")
            }
        }
        Text(desc, style = text.hcDesc, modifier = Modifier.padding(top = 12.dp))

        // footer
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .height(1.dp)
                .background(onDark.copy(alpha = 0.14f)),
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Keep your salary", style = text.hcfK, color = onDark.copy(alpha = 0.5f))
                Row(
                    Modifier.padding(top = 3.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(money(r.sameSalaryLocal, dest), style = text.hcfV, color = onDark)
                    Text(
                        "/mo",
                        style = text.hcfK,
                        color = onDark.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 2.dp),
                    )
                }
            }
            // pill: powerUp -> green (pillDown), !powerUp -> terra (pillUp)
            val pillBg = if (r.powerUp) KamColors.pillDownBg else KamColors.pillUpBg
            val pillFg = if (r.powerUp) KamColors.pillDownFg else KamColors.pillUpFg
            Box(
                Modifier
                    .clip(RoundedCornerShape(13.dp))
                    .background(pillBg)
                    .padding(horizontal = 13.dp, vertical = 8.dp),
            ) {
                Text(
                    "${if (r.powerUp) "+" else ""}${r.powerPct.roundToInt()}% buying power",
                    style = text.hcfPill,
                    color = pillFg,
                )
            }
        }
    }
}

private fun AnnotatedString.Builder.bold(s: String) {
    withStyle(SpanStyle(color = KamColors.ink, fontWeight = FontWeight.SemiBold)) { append(s) }
}
