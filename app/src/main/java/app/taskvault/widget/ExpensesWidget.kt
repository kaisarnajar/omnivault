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
import app.taskvault.data.local.ExpenseEntity
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.map
import java.util.Calendar

class ExpensesWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )
        val expenseRepository = entryPoint.expenseRepository()

        provideContent {
            val expenses by expenseRepository.getExpenses().map { list ->
                // Filter for this month's expenses
                val cal = Calendar.getInstance()
                val currentMonth = cal.get(Calendar.MONTH)
                val currentYear = cal.get(Calendar.YEAR)

                list.filter {
                    cal.timeInMillis = it.timestamp
                    cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
                }
            }.collectAsState(initial = emptyList())

            ExpensesWidgetContent(expenses)
        }
    }

    @Composable
    private fun ExpensesWidgetContent(expenses: List<ExpenseEntity>) {
        val totalExpenses = expenses.sumOf { it.amount }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color.White))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "This Month's Spend",
                style = TextStyle(
                    color = ColorProvider(Color.Black),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = GlanceModifier.height(8.dp))

            Text(
                text = "$%.2f".format(totalExpenses),
                style = TextStyle(
                    color = ColorProvider(Color.Red),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            )
        }
    }
}
