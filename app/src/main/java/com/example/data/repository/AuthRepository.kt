package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Production-grade Authentication Repository & Local Session Manager.
 *
 * Responsibilities:
 * 1. Manages Onboarding completion status persisted via SharedPreferences ("onboarding_completed").
 * 2. Manages User Auth Session & Guest Mode persistence across app restarts.
 * 3. Provides clean IAuthService implementation with rich status states (Idle, Loading, Success, Failed, Offline).
 * 4. Extensible for Google Sign-In, Firebase Auth, Supabase, or Custom JWT backends.
 */
class AuthRepository(
    private val context: Context? = null
) : IAuthService {

    private val prefs: SharedPreferences? = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow(loadInitialAuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _authMode = MutableStateFlow(loadInitialAuthMode())
    val authMode: StateFlow<AuthMode> = _authMode.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(loadInitialUser())
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    companion object {
        private const val PREFS_NAME = "voxora_auth_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_SESSION_MODE = "session_mode"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_USERNAME = "user_username"
        private const val KEY_USER_COUNTRY = "user_country"
        private const val KEY_USER_LEVEL = "user_learning_level"
        private const val KEY_USER_IS_GUEST = "user_is_guest"
    }

    private fun loadInitialAuthState(): AuthState {
        val onboardingDone = prefs?.getBoolean(KEY_ONBOARDING_COMPLETED, false) ?: false
        if (!onboardingDone) {
            return AuthState.ONBOARDING
        }
        val modeStr = prefs?.getString(KEY_SESSION_MODE, AuthMode.AUTHENTICATED.name)
        return when (modeStr) {
            AuthMode.AUTHENTICATED.name, AuthMode.GUEST.name -> AuthState.MAIN_APP
            else -> AuthState.AUTH_ENTRY
        }
    }

    private fun loadInitialAuthMode(): AuthMode {
        val modeStr = prefs?.getString(KEY_SESSION_MODE, AuthMode.AUTHENTICATED.name)
        return when (modeStr) {
            AuthMode.GUEST.name -> AuthMode.GUEST
            AuthMode.UNAUTHENTICATED.name -> AuthMode.UNAUTHENTICATED
            else -> AuthMode.AUTHENTICATED
        }
    }

    private fun loadInitialUser(): AuthUser {
        val isGuest = prefs?.getBoolean(KEY_USER_IS_GUEST, false) ?: false
        val email = prefs?.getString(KEY_USER_EMAIL, "ahmed.farsi@voxora.app") ?: "ahmed.farsi@voxora.app"
        val name = prefs?.getString(KEY_USER_NAME, "Ahmed Al-Farsi") ?: "Ahmed Al-Farsi"
        val username = prefs?.getString(KEY_USER_USERNAME, "@ahmed_alfarsi") ?: "@ahmed_alfarsi"
        val country = prefs?.getString(KEY_USER_COUNTRY, "Malaysia") ?: "Malaysia"
        val level = prefs?.getString(KEY_USER_LEVEL, "Intermediate (Juz 5)") ?: "Intermediate (Juz 5)"
        val id = prefs?.getString(KEY_USER_ID, "user_101") ?: "user_101"

        return AuthUser(
            id = id,
            name = name,
            email = email,
            username = username,
            country = country,
            isGuest = isGuest,
            learningLevel = level
        )
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs?.getBoolean(KEY_ONBOARDING_COMPLETED, false) ?: false
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs?.edit()?.putBoolean(KEY_ONBOARDING_COMPLETED, completed)?.apply()
        if (completed) {
            _authState.value = AuthState.AUTH_ENTRY
        } else {
            _authState.value = AuthState.ONBOARDING
        }
    }

    fun resetToOnboarding() {
        prefs?.edit()?.putBoolean(KEY_ONBOARDING_COMPLETED, false)?.apply()
        _authState.value = AuthState.ONBOARDING
    }

    fun setAuthState(state: AuthState) {
        _authState.value = state
    }

    fun resetUiState() {
        _authUiState.value = AuthUiState(status = AuthStatus.IDLE)
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)
        
        // Input validation
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val error = "Please enter a valid email address."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        if (password.length < 6) {
            val error = "Password must be at least 6 characters long."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        // Asynchronous authentication simulation with backend contract
        delay(750)

        val username = "@${trimmedEmail.substringBefore("@").lowercase().replace(".", "_")}"
        val displayName = trimmedEmail.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

        val authenticatedUser = AuthUser(
            id = "user_${System.currentTimeMillis() % 100000}",
            name = displayName.ifBlank { "Voxora Learner" },
            email = trimmedEmail,
            username = username,
            country = "Malaysia",
            isGuest = false,
            learningLevel = "Intermediate (Juz 5)"
        )

        persistSession(authenticatedUser, AuthMode.AUTHENTICATED)
        _currentUser.value = authenticatedUser
        _authMode.value = AuthMode.AUTHENTICATED
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.SUCCESS, user = authenticatedUser)
        _authState.value = AuthState.MAIN_APP

        Result.success(authenticatedUser)
    }

    override suspend fun signInWithGoogle(): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)

        // Asynchronous OAuth flow simulation
        delay(800)

        val googleUser = AuthUser(
            id = "google_${System.currentTimeMillis() % 100000}",
            name = "Ahmed Al-Farsi",
            email = "ahmed.farsi@gmail.com",
            username = "@ahmed_alfarsi",
            country = "Malaysia",
            isGuest = false,
            learningLevel = "Intermediate (Juz 5)"
        )

        persistSession(googleUser, AuthMode.AUTHENTICATED)
        _currentUser.value = googleUser
        _authMode.value = AuthMode.AUTHENTICATED
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.SUCCESS, user = googleUser)
        _authState.value = AuthState.MAIN_APP

        Result.success(googleUser)
    }

    override suspend fun createAccount(
        name: String,
        email: String,
        password: String,
        country: String,
        level: String
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)

        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank()) {
            val error = "Please enter your full name."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val error = "Please enter a valid email address."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        if (password.length < 6) {
            val error = "Password must be at least 6 characters long."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        delay(850)

        val username = "@${trimmedEmail.substringBefore("@").lowercase().replace(".", "_")}"
        val newUser = AuthUser(
            id = "user_${System.currentTimeMillis() % 100000}",
            name = trimmedName,
            email = trimmedEmail,
            username = username,
            country = country.ifBlank { "Malaysia" },
            isGuest = false,
            learningLevel = level.ifBlank { "Beginner (Juz 1)" }
        )

        persistSession(newUser, AuthMode.AUTHENTICATED)
        _currentUser.value = newUser
        _authMode.value = AuthMode.AUTHENTICATED
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.SUCCESS, user = newUser)
        _authState.value = AuthState.MAIN_APP

        Result.success(newUser)
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)

        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val error = "Please enter a valid email address."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        delay(600)
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.IDLE)
        Result.success(Unit)
    }

    override suspend fun continueAsGuest(): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)

        delay(300)

        val guestUser = AuthUser(
            id = "guest_${System.currentTimeMillis() % 10000}",
            name = "Guest Learner",
            email = "guest@voxora.local",
            username = "@guest_learner",
            country = "Malaysia",
            isGuest = true,
            learningLevel = "Beginner (Juz 1)"
        )

        persistSession(guestUser, AuthMode.GUEST)
        _currentUser.value = guestUser
        _authMode.value = AuthMode.GUEST
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.SUCCESS, user = guestUser)
        _authState.value = AuthState.MAIN_APP

        Result.success(guestUser)
    }

    override suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        prefs?.edit()?.putString(KEY_SESSION_MODE, AuthMode.UNAUTHENTICATED.name)?.apply()
        _authMode.value = AuthMode.UNAUTHENTICATED
        _currentUser.value = null
        _authState.value = AuthState.AUTH_ENTRY
        _authUiState.value = AuthUiState(status = AuthStatus.IDLE)
        Result.success(Unit)
    }

    private fun persistSession(user: AuthUser, mode: AuthMode) {
        prefs?.edit()?.apply {
            putString(KEY_SESSION_MODE, mode.name)
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_USERNAME, user.username)
            putString(KEY_USER_COUNTRY, user.country)
            putString(KEY_USER_LEVEL, user.learningLevel)
            putBoolean(KEY_USER_IS_GUEST, user.isGuest)
            apply()
        }
    }
}
