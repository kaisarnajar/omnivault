package app.taskvault.data.repository

import app.taskvault.data.local.ExpenseDao
import app.taskvault.data.local.ExpenseEntity
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
import app.taskvault.domain.ExpenseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val authRepository: AuthRepository
) : ExpenseRepository {

    override fun getExpenses(): Flow<List<ExpenseEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    expenseDao.getExpensesForUser(userId)
                } else {
                    flowOf(emptyList())
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addExpense(amount: Double, category: String, description: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        val entity = ExpenseEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            amount = amount,
            category = category,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        expenseDao.insertExpense(entity)
    }

    override suspend fun deleteExpense(id: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        expenseDao.deleteExpense(id, userId)
    }
}
