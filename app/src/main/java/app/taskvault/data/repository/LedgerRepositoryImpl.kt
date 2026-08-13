package app.taskvault.data.repository

import app.taskvault.data.local.LedgerDao
import app.taskvault.data.local.LedgerPersonEntity
import app.taskvault.data.local.LedgerTransactionEntity
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
import app.taskvault.domain.LedgerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class LedgerRepositoryImpl @Inject constructor(
    private val ledgerDao: LedgerDao,
    private val authRepository: AuthRepository
) : LedgerRepository {

    override fun getPersons(): Flow<List<LedgerPersonEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) ledgerDao.getPersonsForUser(userId)
                else flowOf(emptyList())
            } else flowOf(emptyList())
        }
    }

    override suspend fun addPerson(name: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        val entity = LedgerPersonEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            name = name,
            createdAt = System.currentTimeMillis()
        )
        ledgerDao.insertPerson(entity)
    }

    override suspend fun deletePerson(id: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        ledgerDao.deleteTransactionsForPerson(id, userId)
        ledgerDao.deletePerson(id, userId)
    }

    override fun getTransactionsForPerson(personId: String): Flow<List<LedgerTransactionEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) ledgerDao.getTransactionsForPerson(personId, userId)
                else flowOf(emptyList())
            } else flowOf(emptyList())
        }
    }

    override fun getAllTransactions(): Flow<List<LedgerTransactionEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) ledgerDao.getAllTransactionsForUser(userId)
                else flowOf(emptyList())
            } else flowOf(emptyList())
        }
    }

    override suspend fun addTransaction(
        personId: String,
        amount: Double,
        description: String,
        isCredit: Boolean
    ) {
        val userId = authRepository.getCurrentUserId() ?: return
        val entity = LedgerTransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            personId = personId,
            amount = amount,
            description = description,
            isCredit = isCredit,
            timestamp = System.currentTimeMillis()
        )
        ledgerDao.insertTransaction(entity)
    }

    override suspend fun deleteTransaction(id: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        ledgerDao.deleteTransaction(id, userId)
    }

    override suspend fun seedSampleData() {
        val names = listOf("Alice", "Bob", "Charlie")
        for (name in names) {
            val personId = UUID.randomUUID().toString()
            val userId = authRepository.getCurrentUserId() ?: return
            ledgerDao.insertPerson(
                LedgerPersonEntity(
                    id = personId,
                    userId = userId,
                    name = name,
                    createdAt = System.currentTimeMillis()
                )
            )
            // Add a couple transactions per person
            addTransaction(personId, (20..200).random().toDouble(), "Lunch split", true)
            addTransaction(personId, (10..150).random().toDouble(), "Movie tickets", false)
        }
    }
}
