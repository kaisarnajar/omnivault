package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val durationInMinutes: Int,
    val timestamp: Long
)
