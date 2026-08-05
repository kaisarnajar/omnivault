package app.taskvault.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.NoteEntity
import app.taskvault.domain.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getNotes().collect {
                _notes.value = it
            }
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }
}
