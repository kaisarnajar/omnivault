package app.taskvault.ui.fitness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.FitnessActivityEntity
import app.taskvault.domain.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class TodayFitnessSummary(
    val totalActiveMinutes: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val totalWorkouts: Int = 0
)

@HiltViewModel
class FitnessViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {

    private val allActivities = repository.getActivities()
    val selectedFilter = MutableStateFlow<String?>("All")

    val activities: StateFlow<List<FitnessActivityEntity>> = combine(allActivities, selectedFilter) { list, filter ->
        if (filter.isNullOrBlank() || filter == "All") {
            list
        } else {
            list.filter { it.activityType.equals(filter, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaySummary: StateFlow<TodayFitnessSummary> = allActivities.map { list ->
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_YEAR)
        val currentYear = cal.get(Calendar.YEAR)

        val todayList = list.filter { entry ->
            val entryCal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            entryCal.get(Calendar.DAY_OF_YEAR) == currentDay && entryCal.get(Calendar.YEAR) == currentYear
        }

        TodayFitnessSummary(
            totalActiveMinutes = todayList.sumOf { it.durationMinutes },
            totalDistanceKm = todayList.sumOf { it.distanceKm },
            totalWorkouts = todayList.count { it.activityType.equals("Workout", ignoreCase = true) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayFitnessSummary())

    fun selectFilter(filter: String?) {
        selectedFilter.value = filter
    }

    fun addActivity(
        activityType: String,
        title: String,
        targetMuscle: String,
        distanceKm: Double,
        durationMinutes: Int,
        caloriesBurned: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addActivity(
                activityType = activityType,
                title = title,
                targetMuscle = targetMuscle,
                distanceKm = distanceKm,
                durationMinutes = durationMinutes,
                caloriesBurned = caloriesBurned,
                notes = notes
            )
        }
    }

    fun updateActivity(
        id: String,
        activityType: String,
        title: String,
        targetMuscle: String,
        distanceKm: Double,
        durationMinutes: Int,
        caloriesBurned: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.updateActivity(
                id = id,
                activityType = activityType,
                title = title,
                targetMuscle = targetMuscle,
                distanceKm = distanceKm,
                durationMinutes = durationMinutes,
                caloriesBurned = caloriesBurned,
                notes = notes
            )
        }
    }

    fun deleteActivity(id: String) {
        viewModelScope.launch {
            repository.deleteActivity(id)
        }
    }
}
