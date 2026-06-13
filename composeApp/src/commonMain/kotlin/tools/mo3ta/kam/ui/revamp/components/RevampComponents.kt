package tools.mo3ta.kam.ui.revamp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import kotlin.math.abs
import kotlin.math.roundToInt
import tools.mo3ta.kam.data.City
import tools.mo3ta.kam.domain.CostCategory
import tools.mo3ta.kam.domain.categoryIndex
import tools.mo3ta.kam.ui.theme.KamColors
import tools.mo3ta.kam.ui.theme.KamDims
import tools.mo3ta.kam.ui.theme.LocalKamText

private val FastOut = CubicBezierEasing(0.7f, 0f, 0.2f, 1f)
private val SheetEasing = CubicBezierEasing(0.6f, 0f, 0.2f, 1f)

/** 2-letter country-code chip used in place of flags. */
@Composable
fun RegionChip(cc: String, size: Dp = 28.dp) {
    val text = LocalKamText.current
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(9.dp))
            .background(KamColors.card2)
            .border(1.dp, KamColors.line, RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = cc.uppercase(),
            style = text.regionChip.copy(fontSize = (size.value * 0.38f).sp),
            color = KamColors.ink2,
        )
    }
}

/** Diverging purchasing-power bar. pct > 0 = money goes further (green right). */
@Composable
fun PowerBar(pct: Double) {
    val text = LocalKamText.current
    val clamped = pct.coerceIn(-80.0, 160.0)
    val span = 240.0
    val zeroFrac = (0.0 - (-80.0)) / span        // baseline position
    val valFrac = (clamped - (-80.0)) / span
    val up = pct >= 0
    val animFrac by animateFloatAsState(valFrac.toFloat(), tween(450, easing = FastOut))
    val fillColor = if (up) KamColors.green else KamColors.terra

    Column(Modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(14.dp),
        ) {
            val r = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            drawRoundRect(color = KamColors.card2, cornerRadius = r)
            val zeroX = (zeroFrac * size.width).toFloat()
            val valX = animFrac * size.width
            val left = minOf(zeroX, valX)
            val w = abs(valX - zeroX)
            if (w > 0f) {
                drawRoundRect(
                    color = fillColor,
                    topLeft = Offset(left, 0f),
                    size = Size(w, size.height),
                    cornerRadius = r,
                )
            }
            // zero marker: 2dp ink line, slightly taller than track
            val markW = 2.dp.toPx()
            drawRoundRect(
                color = KamColors.ink,
                topLeft = Offset(zeroX - markW / 2, -3.dp.toPx()),
                size = Size(markW, size.height + 6.dp.toPx()),
                cornerRadius = CornerRadius(markW / 2, markW / 2),
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Less far", style = text.powerLabels, color = KamColors.ink3)
            Text("Same", style = text.powerLabels, color = KamColors.ink3)
            Text("Further", style = text.powerLabels, color = KamColors.ink3)
        }
    }
}

/** Four-segment lifestyle tier meter with marker dot(s). */
@Composable
fun TierMeter(tierIndex: Int, pos: Float, ghostPos: Float? = null) {
    val text = LocalKamText.current
    val labels = listOf("Basic", "Middle", "Comfortable", "Luxury")
    val animPos by animateFloatAsState(pos, tween(450, easing = FastOut))
    val animGhost by animateFloatAsState(ghostPos ?: 0f, tween(450, easing = FastOut))

    Column(Modifier.fillMaxWidth().padding(top = 9.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val fullWidth = maxWidth
            Row(
                Modifier.fillMaxWidth().height(7.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(4) { i ->
                    val active = i == tierIndex
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (active) KamColors.tier[i] else KamColors.card2),
                    )
                }
            }
            if (ghostPos != null) {
                TierMarker(fullWidth, animGhost, ghost = true)
            }
            TierMarker(fullWidth, animPos, ghost = false)
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            labels.forEachIndexed { i, label ->
                Text(
                    label,
                    style = text.tierSegLabel,
                    color = if (i == tierIndex) KamColors.tier[i] else KamColors.ink3,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TierMarker(fullWidth: Dp, frac: Float, ghost: Boolean) {
    val markerSize = if (ghost) 13.dp else 15.dp
    val x = fullWidth * frac - markerSize / 2
    Box(
        Modifier
            .offset(x = x, y = 4.dp)
            .size(markerSize)
            .clip(RoundedCornerShape(50))
            .then(
                if (ghost) Modifier.background(Color.Transparent).border(2.5.dp, KamColors.ink2, RoundedCornerShape(50))
                else Modifier.background(KamColors.ink).border(3.dp, KamColors.card, RoundedCornerShape(50))
            ),
    )
}

/** Category breakdown: origin (gray) vs dest (green/terra) bars. */
@Composable
fun Breakdown(origin: City, dest: City) {
    val text = LocalKamText.current
    val cats = CostCategory.entries
    val maxIdx = cats.maxOf { maxOf(origin.categoryIndex(it), dest.categoryIndex(it)) }.coerceAtLeast(1.0)

    Column(
        Modifier.fillMaxWidth().padding(top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        cats.forEach { cat ->
            val o = origin.categoryIndex(cat)
            val d = dest.categoryIndex(cat)
            val diff = if (o == 0.0) 0.0 else (d - o) / o * 100
            val cheaper = d < o
            val destColor = if (cheaper) KamColors.green else KamColors.terra
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(cat.label, style = text.bdLabel, color = KamColors.ink2, modifier = Modifier.width(74.dp))
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    BarLine(fraction = (o / maxIdx).toFloat(), color = KamColors.ink3.copy(alpha = 0.55f))
                    BarLine(fraction = (d / maxIdx).toFloat(), color = destColor)
                }
                Text(
                    (if (cheaper) "−" else "+") + abs(diff).roundToInt() + "%",
                    style = text.bdDiff,
                    color = if (cheaper) KamColors.green else KamColors.terra,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(46.dp),
                )
            }
        }
    }
}

@Composable
private fun BarLine(fraction: Float, color: Color) {
    val anim by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(500, easing = FastOut))
    Box(Modifier.fillMaxWidth().height(7.dp)) {
        Box(
            Modifier
                .fillMaxWidth(anim)
                .height(7.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color),
        )
    }
}

/**
 * Custom bottom sheet matching the design (paper bg, grip, scrim). Render it
 * unconditionally and toggle [visible] so enter/exit both animate.
 */
@Composable
fun KamBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    fillHeight: Boolean = false,
    content: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(320)),
            exit = fadeOut(tween(320)),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(KamColors.scrim)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
            )
        }
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(400, easing = SheetEasing)) { it },
            exit = slideOutVertically(tween(400, easing = SheetEasing)) { it },
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .then(if (fillHeight) Modifier.fillMaxHeight(0.88f) else Modifier)
                    .clip(KamDims.sheetShape)
                    .background(KamColors.paper)
                    .padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 30.dp),
            ) {
                // grip
                Box(
                    Modifier
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(38.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(KamColors.ink3.copy(alpha = 0.4f)),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    title,
                    style = LocalKamText.current.sheetTitle,
                    color = KamColors.ink,
                    modifier = Modifier.padding(start = 2.dp, bottom = 12.dp),
                )
                content()
            }
        }
    }
}

/** 3x4 number pad: 1-9, ".", 0, backspace. */
@Composable
fun Keypad(onKey: (String) -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "del"),
    )
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        rows.forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                row.forEach { key ->
                    KeypadKey(key, Modifier.weight(1f)) { onKey(key) }
                }
            }
        }
    }
}

@Composable
private fun KeypadKey(key: String, modifier: Modifier, onClick: () -> Unit) {
    val text = LocalKamText.current
    Box(
        modifier
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(KamColors.card)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (key == "del") {
            Canvas(Modifier.size(26.dp, 20.dp)) {
                val s = Stroke(width = 1.6.dp.toPx())
                val w = size.width
                val h = size.height
                // outline of backspace key
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.31f, h * 0.1f)
                    lineTo(w * 0.92f, h * 0.1f)
                    lineTo(w * 0.92f, h * 0.9f)
                    lineTo(w * 0.31f, h * 0.9f)
                    lineTo(w * 0.04f, h * 0.5f)
                    close()
                }
                drawPath(path, KamColors.ink2, style = s)
                // X
                drawLine(KamColors.ink2, Offset(w * 0.46f, h * 0.35f), Offset(w * 0.73f, h * 0.65f), strokeWidth = 1.6.dp.toPx())
                drawLine(KamColors.ink2, Offset(w * 0.73f, h * 0.35f), Offset(w * 0.46f, h * 0.65f), strokeWidth = 1.6.dp.toPx())
            }
        } else {
            Text(key, style = text.kpKey, color = KamColors.ink)
        }
    }
}
