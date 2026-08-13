package app.taskvault.data.repository

import app.taskvault.data.local.BookmarkDao
import app.taskvault.data.local.BookmarkEntity
import app.taskvault.domain.AuthRepository
import app.taskvault.domain.AuthState
import app.taskvault.domain.BookmarkRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val authRepository: AuthRepository
) : BookmarkRepository {

    override fun getBookmarks(): Flow<List<BookmarkEntity>> {
        return authRepository.authState.flatMapLatest { authState ->
            if (authState is AuthState.Authenticated) {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    bookmarkDao.getBookmarksForUser(userId)
                } else {
                    flowOf(emptyList())
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addBookmark(title: String, url: String, category: String, notes: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        var formattedUrl = url.trim()
        if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
            formattedUrl = "https://$formattedUrl"
        }
        val entity = BookmarkEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = title.ifBlank { formattedUrl },
            url = formattedUrl,
            category = category,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        bookmarkDao.insertBookmark(entity)
    }

    override suspend fun deleteBookmark(id: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        bookmarkDao.deleteBookmark(id, userId)
    }

    override suspend fun seedSampleData() {
        val sampleBookmarks = listOf(
            Tuple4("Android Developers Documentation", "https://developer.android.com", "Tech", "Official Kotlin & Compose guides"),
            Tuple4("Kotlin Programming Language", "https://kotlinlang.org", "Tech", "Language references and updates"),
            Tuple4("Medium - Tech Articles", "https://medium.com", "Reading", "Daily tech and engineering blogs"),
            Tuple4("GitHub - TaskVault Repo", "https://github.com/kaisarnajar/taskvault", "Work", "Project repository and commits"),
            Tuple4("Google Search", "https://google.com", "Personal", "Default search engine")
        )
        for ((title, url, category, notes) in sampleBookmarks) {
            addBookmark(title, url, category, notes)
        }
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
