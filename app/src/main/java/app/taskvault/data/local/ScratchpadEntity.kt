package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scratchpad")
data class ScratchpadEntity(
    @PrimaryKey
    val userId: String,
    val content: String,
    val lastUpdated: Long
)
