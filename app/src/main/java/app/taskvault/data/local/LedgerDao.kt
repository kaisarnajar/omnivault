package app.taskvault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {

    // --- Persons ---
    @Query("SELECT * FROM ledger_persons WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPersonsForUser(userId: String): Flow<List<LedgerPersonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: LedgerPersonEntity)

    @Query("DELETE FROM ledger_persons WHERE id = :id AND userId = :userId")
    suspend fun deletePerson(id: String, userId: String)

    // --- Transactions ---
    @Query("SELECT * FROM ledger_transactions WHERE personId = :personId AND userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForPerson(personId: String, userId: String): Flow<List<LedgerTransactionEntity>>

    @Query("SELECT * FROM ledger_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllTransactionsForUser(userId: String): Flow<List<LedgerTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: LedgerTransactionEntity)

    @Query("DELETE FROM ledger_transactions WHERE id = :id AND userId = :userId")
    suspend fun deleteTransaction(id: String, userId: String)

    @Query("DELETE FROM ledger_transactions WHERE personId = :personId AND userId = :userId")
    suspend fun deleteTransactionsForPerson(personId: String, userId: String)
}
