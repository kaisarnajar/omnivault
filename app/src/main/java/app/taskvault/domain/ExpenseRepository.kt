package app.taskvault.domain

import app.taskvault.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpenses(): Flow<List<ExpenseEntity>>
    suspend fun addExpense(amount: Double, category: String, description: String)
    suspend fun deleteExpense(id: String)
}
