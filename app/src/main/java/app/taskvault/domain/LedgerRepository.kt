package app.taskvault.domain

import app.taskvault.data.local.LedgerPersonEntity
import app.taskvault.data.local.LedgerTransactionEntity
import kotlinx.coroutines.flow.Flow

interface LedgerRepository {
    fun getPersons(): Flow<List<LedgerPersonEntity>>
    suspend fun addPerson(name: String)
    suspend fun deletePerson(id: String)
    fun getTransactionsForPerson(personId: String): Flow<List<LedgerTransactionEntity>>
    fun getAllTransactions(): Flow<List<LedgerTransactionEntity>>
    suspend fun addTransaction(personId: String, amount: Double, description: String, isCredit: Boolean)
    suspend fun deleteTransaction(id: String)
    suspend fun seedSampleData()
}
