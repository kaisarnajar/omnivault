package app.taskvault.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.MoodEntryEntity
import app.taskvault.domain.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val repository: MoodRepository
) : ViewModel() {

    val moods: StateFlow<List<MoodEntryEntity>> = repository.getMoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMood: StateFlow<MoodEntryEntity?> = moods.map { list ->
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_YEAR)
        val currentYear = cal.get(Calendar.YEAR)

        list.firstOrNull { entry ->
            val entryCal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            entryCal.get(Calendar.DAY_OF_YEAR) == currentDay && entryCal.get(Calendar.YEAR) == currentYear
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addMood(mood: String, emoji: String, note: String) {
        viewModelScope.launch {
            repository.addMood(mood, emoji, note)
        }
    }

    fun deleteMood(id: String) {
        viewModelScope.launch {
            repository.deleteMood(id)
        }
    }
}
