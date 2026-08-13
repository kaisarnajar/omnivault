package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val mood: String,
    val emoji: String,
    val note: String,
    val timestamp: Long
)
