package app.taskvault.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.SleepEntryEntity
import app.taskvault.domain.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SleepSummary(
    val avgDurationHours: Double = 0.0,
    val totalLogs: Int = 0,
    val excellentCount: Int = 0,
    val goodCount: Int = 0
)

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val repository: SleepRepository
) : ViewModel() {

    val sleepEntries: StateFlow<List<SleepEntryEntity>> = repository.getSleepEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepSummary: StateFlow<SleepSummary> = sleepEntries.map { entries ->
        if (entries.isEmpty()) {
            SleepSummary()
        } else {
            val totalMins = entries.sumOf { it.durationMinutes }
            val avgHours = (totalMins.toDouble() / entries.size) / 60.0
            val excellent = entries.count { it.sleepQuality.lowercase() == "excellent" }
            val good = entries.count { it.sleepQuality.lowercase() == "good" }
            SleepSummary(
                avgDurationHours = avgHours,
                totalLogs = entries.size,
                excellentCount = excellent,
                goodCount = good
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SleepSummary())

    fun addSleepEntry(bedtime: Long, wakeTime: Long, quality: String, notes: String) {
        viewModelScope.launch {
            val durationMins = ((wakeTime - bedtime) / (1000 * 60)).toInt().coerceAtLeast(0)
            val entry = SleepEntryEntity(
                bedtime = bedtime,
                wakeTime = wakeTime,
                durationMinutes = durationMins,
                sleepQuality = quality,
                notes = notes,
                timestamp = System.currentTimeMillis()
            )
            repository.insertSleepEntry(entry)
        }
    }

    fun deleteSleepEntry(id: String) {
        viewModelScope.launch {
            repository.deleteSleepEntry(id)
        }
    }
}
