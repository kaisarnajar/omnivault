package app.taskvault.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taskvault.data.local.BookmarkEntity
import app.taskvault.domain.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: BookmarkRepository
) : ViewModel() {

    private val allBookmarks = repository.getBookmarks()
    val selectedCategory = MutableStateFlow<String?>("All")

    val bookmarks: StateFlow<List<BookmarkEntity>> = combine(allBookmarks, selectedCategory) { list, category ->
        if (category.isNullOrBlank() || category == "All") {
            list
        } else {
            list.filter { it.category.equals(category, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: String?) {
        selectedCategory.value = category
    }

    fun addBookmark(title: String, url: String, category: String, notes: String) {
        viewModelScope.launch {
            repository.addBookmark(title, url, category, notes)
        }
    }

    fun deleteBookmark(id: String) {
        viewModelScope.launch {
            repository.deleteBookmark(id)
        }
    }
}
