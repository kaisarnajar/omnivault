package app.taskvault.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import app.taskvault.data.local.NoteEntity
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.map

class NotesWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )
        val noteRepository = entryPoint.noteRepository()

        provideContent {
            val notes by noteRepository.getNotes().map { it.take(3) }.collectAsState(initial = emptyList())
            NotesWidgetContent(notes)
        }
    }

    @Composable
    private fun NotesWidgetContent(notes: List<NoteEntity>) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color.White))
                .padding(16.dp)
        ) {
            Text(
                text = "Recent Notes",
                style = TextStyle(
                    color = ColorProvider(Color.Black),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = GlanceModifier.height(12.dp))

            if (notes.isEmpty()) {
                Text(
                    text = "No notes yet!",
                    style = TextStyle(
                        color = ColorProvider(Color.Gray)
                    )
                )
            } else {
                LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                    items(notes) { note ->
                        Column(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(
                                text = note.title.ifBlank { "Untitled Note" },
                                style = TextStyle(
                                    color = ColorProvider(Color.Black),
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = note.content,
                                style = TextStyle(
                                    color = ColorProvider(Color.DarkGray),
                                    fontSize = 12.sp
                                ),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
