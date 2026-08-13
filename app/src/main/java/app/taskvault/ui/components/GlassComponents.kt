package app.taskvault.ui.components

import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.taskvault.ui.theme.*

@Composable
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    targetScale: Float = 0.96f
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "pressScale"
    )
    return this.scale(scale)
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    customFill: Color? = null,
    customBorder: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val fill = customFill ?: if (isDark) GlassFillDark else GlassFillLight
    val borderBrush = if (customBorder != null) {
        Brush.linearGradient(listOf(customBorder, customBorder))
    } else if (isDark) {
        Brush.linearGradient(listOf(Color(0x35FFFFFF), Color(0x05FFFFFF)))
    } else {
        Brush.linearGradient(listOf(Color(0x80FFFFFF), Color(0x30FFFFFF)))
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(fill)
            .border(1.dp, borderBrush, shape),
        content = content
    )
}

fun Modifier.gradientBackground(shape: Shape = RoundedCornerShape(8.dp)): Modifier {
    return this
        .clip(shape)
        .background(Brush.linearGradient(colors = listOf(GradientStart, GradientEnd)))
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .pressScale(interactionSource = interactionSource)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (enabled) {
                    Modifier.gradientBackground(RoundedCornerShape(12.dp))
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                }
            )
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FeatureHeaderCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .gradientBackground(RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun OmniVaultBackground(content: @Composable () -> Unit) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val gradientStart = if (isDark) BackgroundGradientStart else LightBackgroundGradientStart
    val gradientEnd = if (isDark) BackgroundGradientEnd else LightBackgroundGradientEnd
    val glow1 = if (isDark) AmbientGlowDark1 else AmbientGlowLight1
    val glow2 = if (isDark) AmbientGlowDark2 else AmbientGlowLight2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(gradientStart, gradientEnd)))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = glow1,
                radius = size.width * 0.65f,
                center = Offset(size.width * 0.85f, size.height * 0.12f)
            )
            drawCircle(
                color = glow2,
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.15f, size.height * 0.82f)
            )
        }
        content()
    }
}
