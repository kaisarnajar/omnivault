package app.taskvault.domain

import kotlinx.coroutines.flow.Flow

sealed class AuthState {
    object Authenticated : AuthState()

    object Unauthenticated : AuthState()
}

interface AuthRepository {
    val authState: Flow<AuthState>

    suspend fun login(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun register(
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun logout()

    fun getCurrentUserId(): String?
}
