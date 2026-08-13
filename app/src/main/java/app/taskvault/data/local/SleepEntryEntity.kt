package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "sleep_entries")
data class SleepEntryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val bedtime: Long,
    val wakeTime: Long,
    val durationMinutes: Int,
    val sleepQuality: String = "Good", // Excellent, Good, Fair, Poor
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
