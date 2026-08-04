package app.taskvault.ui.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.repository.PomodoroPreferencesRepository
import app.taskvault.domain.PomodoroMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PomodoroViewModel(
    private val preferencesRepository: PomodoroPreferencesRepository
) : ViewModel() {

    private val _currentMode = MutableStateFlow(PomodoroMode.POMODORO)
    val currentMode: StateFlow<PomodoroMode> = _currentMode.asStateFlow()

    private val _timeRemaining = MutableStateFlow(25 * 60)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // Expose flow of settings
    val pomodoroDuration = preferencesRepository.pomodoroDuration
    val shortBreakDuration = preferencesRepository.shortBreakDuration
    val longBreakDuration = preferencesRepository.longBreakDuration

    private val _timerEvent = kotlinx.coroutines.flow.MutableSharedFlow<Unit>()
    val timerEvent: kotlinx.coroutines.flow.SharedFlow<Unit> = _timerEvent.asSharedFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val defaultDuration = preferencesRepository.pomodoroDuration.first()
            _timeRemaining.value = defaultDuration * 60
        }
    }

    fun setMode(mode: PomodoroMode) {
        if (_currentMode.value != mode) {
            pauseTimer()
            _currentMode.value = mode
            resetTimer()
        }
    }

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
            if (_timeRemaining.value == 0 && _isPlaying.value) {
                _isPlaying.value = false
                _timerEvent.emit(Unit)
            }
        }
    }

    private fun pauseTimer() {
        _isPlaying.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        viewModelScope.launch {
            val durationInMinutes = when (_currentMode.value) {
                PomodoroMode.POMODORO -> preferencesRepository.pomodoroDuration.first()
                PomodoroMode.SHORT_BREAK -> preferencesRepository.shortBreakDuration.first()
                PomodoroMode.LONG_BREAK -> preferencesRepository.longBreakDuration.first()
            }
            _timeRemaining.value = durationInMinutes * 60
        }
    }

    fun saveSettings(pomodoro: Int, shortBreak: Int, longBreak: Int) {
        viewModelScope.launch {
            preferencesRepository.savePomodoroDuration(pomodoro)
            preferencesRepository.saveShortBreakDuration(shortBreak)
            preferencesRepository.saveLongBreakDuration(longBreak)
            if (!_isPlaying.value) {
                resetTimer()
            }
        }
    }
}
