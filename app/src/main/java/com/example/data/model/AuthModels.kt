package com.example.data.model

enum class AuthState {
    SPLASH,
    ONBOARDING,
    AUTH_ENTRY,
    MAIN_APP
}

enum class AuthMode {
    AUTHENTICATED,
    GUEST,
    UNAUTHENTICATED
}

enum class AuthStatus {
    IDLE,
    LOADING,
    SUCCESS,
    FAILED,
    CANCELLED,
    OFFLINE
}

enum class AuthFormType {
    WELCOME,
    SIGN_IN,
    CREATE_ACCOUNT,
    FORGOT_PASSWORD
}

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val username: String,
    val country: String = "Malaysia",
    val isGuest: Boolean = false,
    val learningLevel: String = "Intermediate (Juz 5)",
    val avatarEmoji: String = "📖"
)

data class AuthUiState(
    val status: AuthStatus = AuthStatus.IDLE,
    val user: AuthUser? = null,
    val errorMessage: String? = null,
    val currentForm: AuthFormType = AuthFormType.WELCOME
)

/**
 * Clean production-grade Authentication Service interface.
 *
 * BACKEND CONFIGURATION REQUIREMENT:
 * In production, connect this service to your secure backend:
 * - Firebase Authentication (Google, Email/Password, Anonymous Guest)
 * - Custom OAuth2 / OpenID Connect Server (Bearer JWT tokens)
 * - Supabase Auth
 *
 * NOTE: Private API keys, JWT secrets, and client secrets MUST NEVER be hardcoded.
 */
interface IAuthService {
    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signInWithGoogle(): Result<AuthUser>
    suspend fun createAccount(name: String, email: String, password: String, country: String, level: String): Result<AuthUser>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun continueAsGuest(): Result<AuthUser>
    suspend fun signOut(): Result<Unit>
}
