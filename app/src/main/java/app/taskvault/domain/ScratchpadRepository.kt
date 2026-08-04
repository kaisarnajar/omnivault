package app.taskvault.domain

import kotlinx.coroutines.flow.Flow

interface ScratchpadRepository {
    fun getScratchpadContent(): Flow<String>
    suspend fun saveScratchpadContent(content: String)
}
