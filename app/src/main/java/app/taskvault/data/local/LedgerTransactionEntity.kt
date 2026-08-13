package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ledger_transactions")
data class LedgerTransactionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val personId: String,
    val amount: Double,
    val description: String,
    val isCredit: Boolean, // true = they owe you, false = you owe them
    val timestamp: Long
)
