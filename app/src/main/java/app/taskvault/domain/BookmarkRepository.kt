package app.taskvault.domain

import app.taskvault.data.local.BookmarkEntity
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarks(): Flow<List<BookmarkEntity>>
    suspend fun addBookmark(title: String, url: String, category: String, notes: String)
    suspend fun deleteBookmark(id: String)
    suspend fun seedSampleData()
}
