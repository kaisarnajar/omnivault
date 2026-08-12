package app.taskvault.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.domain.NoteRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: String = savedStateHandle.get<String>("noteId") ?: ""

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    init {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            if (note != null) {
                _title.value = note.title
                _content.value = note.content
            }
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
