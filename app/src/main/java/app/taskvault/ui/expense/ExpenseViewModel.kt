package app.taskvault.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.ExpenseEntity
import app.taskvault.domain.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseEntity>> = repository.getExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayTotal: StateFlow<Double> = expenses.map { list ->
        val now = System.currentTimeMillis()
        list.filter { isSameDay(it.timestamp, now) }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val thisWeekTotal: StateFlow<Double> = expenses.map { list ->
        val now = System.currentTimeMillis()
        list.filter { isSameWeek(it.timestamp, now) }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val currentMonthTotal: StateFlow<Double> = expenses.map { list ->
        val now = System.currentTimeMillis()
        list.filter { isSameMonth(it.timestamp, now) }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val thisYearTotal: StateFlow<Double> = expenses.map { list ->
        val now = System.currentTimeMillis()
        list.filter { isSameYear(it.timestamp, now) }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun addExpense(amount: Double, category: String, description: String) {
        viewModelScope.launch {
            repository.addExpense(amount, category, description)
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }
}

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
        c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

private fun isSameWeek(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
        c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR)
}

private fun isSameMonth(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
        c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
}

private fun isSameYear(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
}
