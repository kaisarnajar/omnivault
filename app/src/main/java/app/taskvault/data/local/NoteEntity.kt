package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val lastUpdated: Long
)
