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

enum class VoxoraAuthState {
    SIGNED_OUT,
    GUEST,
    SIGNED_IN,
    LOADING,
    ERROR
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
    val photoUrl: String? = null,
    val country: String = "Malaysia",
    val isGuest: Boolean = false,
    val learningLevel: String = "Intermediate (Juz 5)",
    val avatarEmoji: String = "📖",
    val provider: String = "firebase"
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
 * Supports:
 * - Firebase Authentication (Google via Credential Manager, Email/Password, Anonymous Guest)
 * - Session restoration & multi-device identity synchronization.
 */
interface IAuthService {
    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signInWithGoogle(context: android.content.Context? = null): Result<AuthUser>
    suspend fun createAccount(name: String, email: String, password: String, country: String, level: String): Result<AuthUser>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun continueAsGuest(): Result<AuthUser>
    suspend fun signOut(context: android.content.Context? = null): Result<Unit>
}
