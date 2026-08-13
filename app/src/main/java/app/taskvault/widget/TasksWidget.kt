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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import app.taskvault.domain.Todo
import dagger.hilt.android.EntryPointAccessors

class TasksWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )
        val todoRepository = entryPoint.todoRepository()

        provideContent {
            val todos by todoRepository.getTodos().collectAsState(initial = emptyList())
            val incompleteTodos = todos.filter { !it.isCompleted }.take(5)

            TasksWidgetContent(incompleteTodos)
        }
    }

    @Composable
    private fun TasksWidgetContent(todos: List<Todo>) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color.White))
                .padding(16.dp)
        ) {
            Text(
                text = "Pending Tasks",
                style = TextStyle(
                    color = ColorProvider(Color.Black),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = GlanceModifier.height(12.dp))

            if (todos.isEmpty()) {
                Text(
                    text = "No pending tasks!",
                    style = TextStyle(
                        color = ColorProvider(Color.Gray)
                    )
                )
            } else {
                LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                    items(todos) { todo ->
                        Row(
                            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${todo.title}",
                                style = TextStyle(
                                    color = ColorProvider(Color.DarkGray)
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
