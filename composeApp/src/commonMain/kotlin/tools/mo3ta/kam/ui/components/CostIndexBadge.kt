package tools.mo3ta.kam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CostIndexBadge(
    index: Double,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when {
        index < 30 -> Color(0xFF1B5E20).copy(alpha = 0.85f) to Color(0xFFA5D6A7)
        index < 50 -> Color(0xFF2E7D32).copy(alpha = 0.85f) to Color(0xFFC8E6C9)
        index < 70 -> Color(0xFFF57F17).copy(alpha = 0.85f) to Color(0xFFFFF9C4)
        index < 90 -> Color(0xFFE65100).copy(alpha = 0.85f) to Color(0xFFFFCC80)
        else -> Color(0xFFB71C1C).copy(alpha = 0.85f) to Color(0xFFFFCDD2)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "%.1f".format(index),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
