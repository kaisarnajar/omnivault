package app.taskvault.domain

import app.taskvault.data.local.FitnessActivityEntity
import kotlinx.coroutines.flow.Flow

interface FitnessRepository {
    fun getActivities(): Flow<List<FitnessActivityEntity>>
    suspend fun addActivity(
        activityType: String,
        title: String,
        targetMuscle: String,
        distanceKm: Double,
        durationMinutes: Int,
        caloriesBurned: Int,
        notes: String
    )
    suspend fun deleteActivity(id: String)
    suspend fun seedSampleData()
}
