package app.taskvault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EisenhowerSelector(
    selectedTag: String,
    onTagSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "EISENHOWER MATRIX",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        ) {
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                QuadrantCard(
                    modifier = Modifier.weight(1f).aspectRatio(1.5f),
                    title = "Do",
                    subtitle = "Urgent & Important",
                    color = Color(0xFFE57373), // Red
                    isSelected = selectedTag == "Do",
                    onClick = { onTagSelect(if (selectedTag == "Do") "" else "Do") }
                )
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
                QuadrantCard(
                    modifier = Modifier.weight(1f).aspectRatio(1.5f),
                    title = "Schedule",
                    subtitle = "Not Urgent & Important",
                    color = Color(0xFF64B5F6), // Blue
                    isSelected = selectedTag == "Schedule",
                    onClick = { onTagSelect(if (selectedTag == "Schedule") "" else "Schedule") }
                )
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                QuadrantCard(
                    modifier = Modifier.weight(1f).aspectRatio(1.5f),
                    title = "Delegate",
                    subtitle = "Urgent & Not Important",
                    color = Color(0xFF81C784), // Green
                    isSelected = selectedTag == "Delegate",
                    onClick = { onTagSelect(if (selectedTag == "Delegate") "" else "Delegate") }
                )
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
                QuadrantCard(
                    modifier = Modifier.weight(1f).aspectRatio(1.5f),
                    title = "Delete",
                    subtitle = "Not Urgent & Not Important",
                    color = Color(0xFF9E9E9E), // Gray
                    isSelected = selectedTag == "Delete",
                    onClick = { onTagSelect(if (selectedTag == "Delete") "" else "Delete") }
                )
            }
        }
    }
}

@Composable
private fun QuadrantCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
    val borderColor = if (isSelected) color else Color.Transparent

    Box(
        modifier = modifier
            .background(backgroundColor)
            .border(2.dp, borderColor)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
