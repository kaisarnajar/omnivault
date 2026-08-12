package app.taskvault.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.ExpenseEntity
import app.taskvault.domain.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar


import dagger.hilt.android.lifecycle.HiltViewModel
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

    val currentMonthTotal: StateFlow<Double> = expenses.map { list ->
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        list.filter { expense ->
            val expenseCalendar = Calendar.getInstance().apply { timeInMillis = expense.timestamp }
            expenseCalendar.get(Calendar.MONTH) == currentMonth && 
            expenseCalendar.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }
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
