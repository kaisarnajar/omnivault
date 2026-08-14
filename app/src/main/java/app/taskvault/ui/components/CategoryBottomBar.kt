package app.taskvault.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ToolCategory(
    val displayName: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    ALL("All", Icons.Default.GridView, Color(0xFF6366F1)),         // Indigo Accent
    PRODUCTIVITY("Work", Icons.Default.FlashOn, Color(0xFFF97316)),  // Vibrant Orange Accent
    FINANCE("Finance", Icons.Default.Shield, Color(0xFF10B981)),   // Emerald Accent
    HEALTH("Health", Icons.Default.Favorite, Color(0xFFEC4899)),   // Rose Pink Accent
    UTILITIES("Tools", Icons.Default.Build, Color(0xFF3B82F6))      // Royal Blue Accent
}

@Composable
fun CategoryBottomBar(
    selectedCategory: ToolCategory,
    onCategorySelected: (ToolCategory) -> Unit,
    categoryCounts: Map<ToolCategory, Int>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolCategory.values().forEach { category ->
                    val isSelected = selectedCategory == category
                    val activeBgColor by animateColorAsState(
                        targetValue = if (isSelected) category.accentColor.copy(alpha = 0.20f) else Color.Transparent,
                        label = "activeBgColor"
                    )
                    val activeContentColor by animateColorAsState(
                        targetValue = if (isSelected) category.accentColor else category.accentColor.copy(alpha = 0.65f),
                        label = "activeContentColor"
                    )

                    val interactionSource = remember { MutableInteractionSource() }
                    val count = categoryCounts[category] ?: 0

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(18.dp))
                            .background(activeBgColor)
                            .pressScale(interactionSource = interactionSource)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onCategorySelected(category) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            BadgedBox(
                                badge = {
                                    if (count > 0) {
                                        Badge(
                                            containerColor = if (isSelected) category.accentColor else category.accentColor.copy(alpha = 0.25f),
                                            contentColor = if (isSelected) Color.White else category.accentColor
                                        ) {
                                            Text(
                                                text = "$count",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.displayName,
                                    tint = activeContentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = activeContentColor,
                                fontSize = 11.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
