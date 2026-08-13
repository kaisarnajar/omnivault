package app.taskvault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import app.taskvault.ui.theme.GlassBorderDark
import app.taskvault.ui.theme.GlassBorderLight
import app.taskvault.ui.theme.GlassFillDark
import app.taskvault.ui.theme.GlassFillLight
import app.taskvault.ui.theme.GradientEnd
import app.taskvault.ui.theme.GradientStart

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    customFill: Color? = null,
    customBorder: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    // Fallback to theme colors if custom colors are not provided
    val fill = customFill ?: if (isDark) GlassFillDark else GlassFillLight
    val border = customBorder ?: if (isDark) GlassBorderDark else GlassBorderLight

    // To simulate the glass gradient effect in Stitch design, we can use a linear gradient
    // starting slightly more opaque to fully transparent, but for simplicity here we use the fill directly
    // and rely on a blur if we had Android 12+, otherwise just the semi-transparent fill.
    Box(
        modifier = modifier
            .clip(shape)
            .background(fill)
            .border(1.dp, border, shape),
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (enabled) {
                    Modifier.gradientBackground(RoundedCornerShape(12.dp))
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                }
            )
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = if (enabled) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                strokeWidth = 2.dp
            )
        } else {
            androidx.compose.material3.Text(
                text = text,
                color = if (enabled) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun OmniVaultBackground(content: @Composable () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val gradientStart = if (isDark) app.taskvault.ui.theme.BackgroundGradientStart else app.taskvault.ui.theme.LightBackgroundGradientStart
    val gradientEnd = if (isDark) app.taskvault.ui.theme.BackgroundGradientEnd else app.taskvault.ui.theme.LightBackgroundGradientEnd

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(gradientStart, gradientEnd)))
    ) {
        content()
    }
}
