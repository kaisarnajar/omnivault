package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fitness_activities")
data class FitnessActivityEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val activityType: String, // "Workout", "Running", "Sports", "Other"
    val title: String,
    val targetMuscle: String, // "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Full Body", or ""
    val distanceKm: Double, // 0.0 if N/A
    val durationMinutes: Int, // duration in minutes
    val caloriesBurned: Int,
    val notes: String,
    val timestamp: Long
)
