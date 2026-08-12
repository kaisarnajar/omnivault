package app.taskvault.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pomodoro_settings")

class PomodoroPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        val POMODORO_DURATION_KEY = intPreferencesKey("pomodoro_duration")
        val SHORT_BREAK_DURATION_KEY = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION_KEY = intPreferencesKey("long_break_duration")
        
        const val DEFAULT_POMODORO = 25
        const val DEFAULT_SHORT_BREAK = 5
        const val DEFAULT_LONG_BREAK = 15
    }

    val pomodoroDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[POMODORO_DURATION_KEY] ?: DEFAULT_POMODORO
    }

    val shortBreakDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SHORT_BREAK_DURATION_KEY] ?: DEFAULT_SHORT_BREAK
    }

    val longBreakDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LONG_BREAK_DURATION_KEY] ?: DEFAULT_LONG_BREAK
    }

    suspend fun savePomodoroDuration(durationInMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[POMODORO_DURATION_KEY] = durationInMinutes
        }
    }

    suspend fun saveShortBreakDuration(durationInMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[SHORT_BREAK_DURATION_KEY] = durationInMinutes
        }
    }

    suspend fun saveLongBreakDuration(durationInMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[LONG_BREAK_DURATION_KEY] = durationInMinutes
        }
    }
}
