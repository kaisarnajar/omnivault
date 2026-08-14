package app.taskvault.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.domain.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val noteId: String = savedStateHandle.get<String>("noteId") ?: ""
    val initialEditMode: Boolean = savedStateHandle.get<String>("isEdit")?.toBoolean() ?: noteId.isEmpty()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            if (noteId.isNotEmpty()) {
                val note = repository.getNoteById(noteId)
                if (note != null) {
                    _title.value = note.title
                    _content.value = note.content
                }
            }
            _isLoaded.value = true
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
    }

    fun saveNote() {
        viewModelScope.launch {
            if (_title.value.isNotBlank() || _content.value.isNotBlank()) {
                repository.saveNote(noteId, _title.value, _content.value)
            }
        }
    }
}
