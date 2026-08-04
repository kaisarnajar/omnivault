package app.taskvault.ui.scratchpad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.domain.ScratchpadRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ScratchpadViewModel(
    private val repository: ScratchpadRepository
) : ViewModel() {

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()
    
    // We keep track of user input to debounce saving
    private val userInputFlow = MutableStateFlow("")

    init {
        // Load initial content
        viewModelScope.launch {
            repository.getScratchpadContent().collect {
                _content.value = it
                userInputFlow.value = it // Sync the internal flow
            }
        }
        
        // Debounce saving to the database
        viewModelScope.launch {
            userInputFlow
                .debounce(500L) // Wait 500ms after user stops typing
                .collectLatest { text ->
                    if (text != _content.value) { // Avoid infinite loop or redundant saves on load
                        repository.saveScratchpadContent(text)
                    }
                }
        }
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
        userInputFlow.value = newContent
    }
}
