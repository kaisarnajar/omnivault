package app.taskvault.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import app.taskvault.MainActivity
import app.taskvault.data.local.PomodoroSessionEntity
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.map
import java.util.Calendar

class PomodoroWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )
        val pomodoroRepo = entryPoint.pomodoroHistoryRepository()

        provideContent {
            val sessions by pomodoroRepo.getSessions().map { list ->
                // Filter for today's sessions only
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                list.filter { it.timestamp >= today }
            }.collectAsState(initial = emptyList())

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            PomodoroWidgetContent(sessions, intent)
        }
    }

    @Composable
    private fun PomodoroWidgetContent(sessions: List<PomodoroSessionEntity>, launchIntent: Intent) {
        val totalFocusMinutes = sessions.sumOf { it.durationInMinutes }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color.White))
                .padding(16.dp)
                .clickable(actionStartActivity(launchIntent)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Focus",
                style = TextStyle(
                    color = ColorProvider(Color.Black),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            Text(
                text = "${totalFocusMinutes}m",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFE91E63)), // A primary color-ish pink
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            Text(
                text = "Tap to Focus",
                style = TextStyle(
                    color = ColorProvider(Color.Gray),
                    fontSize = 12.sp
                )
            )
        }
    }
}
