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

data class FitnessPeriodSummary(
    val activeMinutes: Int = 0,
    val distanceKm: Double = 0.0,
    val caloriesBurned: Int = 0,
    val workoutCount: Int = 0
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

    val todaySummary: StateFlow<FitnessPeriodSummary> = allActivities.map { list ->
        val now = System.currentTimeMillis()
        createSummary(list.filter { isSameDay(it.timestamp, now) })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FitnessPeriodSummary())

    val thisWeekSummary: StateFlow<FitnessPeriodSummary> = allActivities.map { list ->
        val now = System.currentTimeMillis()
        createSummary(list.filter { isSameWeek(it.timestamp, now) })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FitnessPeriodSummary())

    val thisMonthSummary: StateFlow<FitnessPeriodSummary> = allActivities.map { list ->
        val now = System.currentTimeMillis()
        createSummary(list.filter { isSameMonth(it.timestamp, now) })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FitnessPeriodSummary())

    val thisYearSummary: StateFlow<FitnessPeriodSummary> = allActivities.map { list ->
        val now = System.currentTimeMillis()
        createSummary(list.filter { isSameYear(it.timestamp, now) })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FitnessPeriodSummary())

    private fun createSummary(items: List<FitnessActivityEntity>): FitnessPeriodSummary {
        return FitnessPeriodSummary(
            activeMinutes = items.sumOf { it.durationMinutes },
            distanceKm = items.sumOf { it.distanceKm },
            caloriesBurned = items.sumOf { it.caloriesBurned },
            workoutCount = items.size
        )
    }

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

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
        c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

private fun isSameWeek(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
        c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR)
}

private fun isSameMonth(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
        c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
}

private fun isSameYear(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
}
