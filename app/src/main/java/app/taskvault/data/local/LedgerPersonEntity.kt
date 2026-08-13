package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ledger_persons")
data class LedgerPersonEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val createdAt: Long
)
