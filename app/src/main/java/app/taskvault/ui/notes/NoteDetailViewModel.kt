package app.taskvault.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.domain.NoteRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class NoteDetailViewModel(
    private val repository: NoteRepository,
    private val noteId: String
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val userInputs = MutableStateFlow(Pair("", ""))

    init {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            if (note != null) {
                _title.value = note.title
                _content.value = note.content
                userInputs.value = Pair(note.title, note.content)
            }
        }

        viewModelScope.launch {
            userInputs
                .debounce(500L)
                .collectLatest { (t, c) ->
                    // Only save if there's actual content or title, and it differs from init load
                    if (t.isNotBlank() || c.isNotBlank()) {
                        repository.saveNote(noteId, t, c)
                    }
                }
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
        userInputs.value = Pair(newTitle, _content.value)
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
        userInputs.value = Pair(_title.value, newContent)
    }
}
