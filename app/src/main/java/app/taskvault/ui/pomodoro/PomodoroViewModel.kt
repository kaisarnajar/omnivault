package app.taskvault.ui.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.repository.TodoRepositoryImpl
import app.taskvault.domain.Todo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PomodoroViewModel : ViewModel() {

    private val _timeRemaining = MutableStateFlow(25 * 60) // 25 minutes in seconds
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var timerJob: Job? = null

    fun toggleTimer() {
        if (_isPlaying.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isPlaying.value = true
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0 && _isPlaying.value) {
                delay(1000L)
                _timeRemaining.value -= 1
            }
            if (_timeRemaining.value == 0) {
                _isPlaying.value = false
            }
        }
    }

    private fun pauseTimer() {
        _isPlaying.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timeRemaining.value = 25 * 60
    }
}
