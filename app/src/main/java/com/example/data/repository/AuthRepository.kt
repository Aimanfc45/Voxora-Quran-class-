package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.data.analytics.VoxoraAnalytics
import com.example.data.crashlytics.VoxoraCrashlytics
import com.example.data.firebase.VoxoraFirebaseService
import com.example.data.model.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

/**
 * Production-grade Authentication Repository & Session Manager for Voxora Muslim Centre.
 *
 * Responsibilities:
 * 1. Coordinates real Firebase Authentication (Google Sign-In via Credential Manager & Email/Password).
 * 2. Unifies single source of truth for VoxoraAuthState (SIGNED_OUT, GUEST, SIGNED_IN, LOADING, ERROR).
 * 3. Restores sessions from Firebase currentUser and local SharedPreferences.
 * 4. Integrates privacy-conscious Firebase Analytics and Crashlytics tracking.
 * 5. Handles graceful fallbacks with friendly messages when Firebase/Google Services are pending configuration.
 */
class AuthRepository(
    private val context: Context? = null
) : IAuthService {

    private val prefs: SharedPreferences? = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _voxoraAuthState = MutableStateFlow(loadInitialVoxoraAuthState())
    val voxoraAuthState: StateFlow<VoxoraAuthState> = _voxoraAuthState.asStateFlow()

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
        private const val KEY_USER_PHOTO_URL = "user_photo_url"
        private const val KEY_USER_COUNTRY = "user_country"
        private const val KEY_USER_LEVEL = "user_learning_level"
        private const val KEY_USER_IS_GUEST = "user_is_guest"
        private const val KEY_USER_PROVIDER = "user_auth_provider"
    }

    private fun loadInitialVoxoraAuthState(): VoxoraAuthState {
        val fbUser = VoxoraFirebaseService.getAuth()?.currentUser
        if (fbUser != null) {
            return VoxoraAuthState.SIGNED_IN
        }
        val modeStr = prefs?.getString(KEY_SESSION_MODE, null)
        return when (modeStr) {
            AuthMode.GUEST.name -> VoxoraAuthState.GUEST
            AuthMode.AUTHENTICATED.name -> VoxoraAuthState.SIGNED_IN
            else -> VoxoraAuthState.SIGNED_OUT
        }
    }

    private fun loadInitialAuthState(): AuthState {
        val onboardingDone = prefs?.getBoolean(KEY_ONBOARDING_COMPLETED, false) ?: false
        if (!onboardingDone) {
            return AuthState.ONBOARDING
        }
        val fbUser = VoxoraFirebaseService.getAuth()?.currentUser
        if (fbUser != null) {
            return AuthState.MAIN_APP
        }
        val modeStr = prefs?.getString(KEY_SESSION_MODE, AuthMode.AUTHENTICATED.name)
        return when (modeStr) {
            AuthMode.AUTHENTICATED.name, AuthMode.GUEST.name -> AuthState.MAIN_APP
            else -> AuthState.AUTH_ENTRY
        }
    }

    private fun loadInitialAuthMode(): AuthMode {
        val fbUser = VoxoraFirebaseService.getAuth()?.currentUser
        if (fbUser != null) {
            return AuthMode.AUTHENTICATED
        }
        val modeStr = prefs?.getString(KEY_SESSION_MODE, AuthMode.AUTHENTICATED.name)
        return when (modeStr) {
            AuthMode.GUEST.name -> AuthMode.GUEST
            AuthMode.UNAUTHENTICATED.name -> AuthMode.UNAUTHENTICATED
            else -> AuthMode.AUTHENTICATED
        }
    }

    private fun loadInitialUser(): AuthUser {
        val fbUser = VoxoraFirebaseService.getAuth()?.currentUser
        if (fbUser != null) {
            val email = fbUser.email ?: "reciter@voxora.app"
            val username = "@" + email.substringBefore("@").lowercase().replace(".", "_")
            val name = fbUser.displayName ?: email.substringBefore("@").replace(".", " ")
            return AuthUser(
                id = fbUser.uid,
                name = name,
                email = email,
                username = username,
                photoUrl = fbUser.photoUrl?.toString(),
                country = prefs?.getString(KEY_USER_COUNTRY, "Malaysia") ?: "Malaysia",
                isGuest = false,
                learningLevel = prefs?.getString(KEY_USER_LEVEL, "Intermediate (Juz 5)") ?: "Intermediate (Juz 5)",
                provider = fbUser.providerData.firstOrNull()?.providerId ?: "firebase"
            )
        }

        val isGuest = prefs?.getBoolean(KEY_USER_IS_GUEST, false) ?: false
        val email = prefs?.getString(KEY_USER_EMAIL, "ahmed.farsi@voxora.app") ?: "ahmed.farsi@voxora.app"
        val name = prefs?.getString(KEY_USER_NAME, "Ahmed Al-Farsi") ?: "Ahmed Al-Farsi"
        val username = prefs?.getString(KEY_USER_USERNAME, "@ahmed_alfarsi") ?: "@ahmed_alfarsi"
        val photoUrl = prefs?.getString(KEY_USER_PHOTO_URL, null)
        val country = prefs?.getString(KEY_USER_COUNTRY, "Malaysia") ?: "Malaysia"
        val level = prefs?.getString(KEY_USER_LEVEL, "Intermediate (Juz 5)") ?: "Intermediate (Juz 5)"
        val id = prefs?.getString(KEY_USER_ID, "user_101") ?: "user_101"
        val provider = prefs?.getString(KEY_USER_PROVIDER, if (isGuest) "guest" else "firebase") ?: "firebase"

        return AuthUser(
            id = id,
            name = name,
            email = email,
            username = username,
            photoUrl = photoUrl,
            country = country,
            isGuest = isGuest,
            learningLevel = level,
            provider = provider
        )
    }

    fun isAuthenticated(): Boolean {
        return _authMode.value == AuthMode.AUTHENTICATED && _currentUser.value != null && !_currentUser.value!!.isGuest
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

    override suspend fun signInWithGoogle(context: Context?): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)
        _voxoraAuthState.value = VoxoraAuthState.LOADING

        val appContext = context ?: this@AuthRepository.context
        if (appContext == null) {
            val err = "Application context required for Google Sign-In."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = err)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            return@withContext Result.failure(IllegalStateException(err))
        }

        val firebaseAuth = VoxoraFirebaseService.getAuth()

        // Discover Web Client ID from generated resources (if google-services.json is present)
        val resId = appContext.resources.getIdentifier("default_web_client_id", "string", appContext.packageName)
        val webClientId: String? = if (resId != 0) {
            try { appContext.getString(resId) } catch (_: Exception) { null }
        } else null

        if (webClientId.isNullOrBlank()) {
            val err = "Firebase Google Sign-In requires your Google Web Client ID. Please add your google-services.json from the Firebase Console to enable Google Sign-In."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = err)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            VoxoraAnalytics.logAuthGoogleFailed(appContext, "missing_web_client_id")
            return@withContext Result.failure(IllegalStateException(err))
        }

        if (firebaseAuth == null) {
            val err = "Firebase is not configured yet. Please add your google-services.json configuration file."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = err)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            VoxoraAnalytics.logAuthGoogleFailed(appContext, "firebase_null")
            return@withContext Result.failure(IllegalStateException(err))
        }

        try {
            val credentialManager = CredentialManager.create(appContext)
            val rawNonce = UUID.randomUUID().toString()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(rawNonce.toByteArray())
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(request = request, context = appContext)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                val fbUser = authResult.user ?: throw IllegalStateException("Firebase returned an empty user profile.")

                val user = AuthUser(
                    id = fbUser.uid,
                    name = fbUser.displayName ?: googleIdTokenCredential.displayName ?: "Google User",
                    email = fbUser.email ?: googleIdTokenCredential.id,
                    username = "@" + (fbUser.email?.substringBefore("@") ?: "reciter").lowercase(),
                    photoUrl = fbUser.photoUrl?.toString() ?: googleIdTokenCredential.profilePictureUri?.toString(),
                    country = prefs?.getString(KEY_USER_COUNTRY, "Malaysia") ?: "Malaysia",
                    isGuest = false,
                    learningLevel = prefs?.getString(KEY_USER_LEVEL, "Intermediate (Juz 5)") ?: "Intermediate (Juz 5)",
                    provider = "google.com"
                )

                persistSession(user, AuthMode.AUTHENTICATED)
                _currentUser.value = user
                _authMode.value = AuthMode.AUTHENTICATED
                _voxoraAuthState.value = VoxoraAuthState.SIGNED_IN
                _authUiState.value = _authUiState.value.copy(status = AuthStatus.SUCCESS, user = user)
                _authState.value = AuthState.MAIN_APP

                VoxoraAnalytics.logAuthGoogleSuccess(appContext)
                VoxoraCrashlytics.setUserId(user.id)
                VoxoraCrashlytics.setAppDiagnostics("2.0A.1", "AUTH", "SIGNED_IN")

                Result.success(user)
            } else {
                val err = "Unexpected credential format received."
                _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = err)
                _voxoraAuthState.value = VoxoraAuthState.ERROR
                VoxoraAnalytics.logAuthGoogleFailed(appContext, "unsupported_credential")
                Result.failure(IllegalStateException(err))
            }
        } catch (e: GetCredentialCancellationException) {
            val err = "Google Sign-In was cancelled."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.CANCELLED, errorMessage = err)
            _voxoraAuthState.value = VoxoraAuthState.SIGNED_OUT
            VoxoraAnalytics.logAuthGoogleFailed(appContext, "cancelled")
            Result.failure(e)
        } catch (e: Exception) {
            val err = when (e) {
                is com.google.firebase.FirebaseNetworkException -> "Network error: Please check your internet connection."
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Account not found or has been disabled."
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Google credentials invalid or expired."
                else -> e.localizedMessage ?: "Google Sign-In failed. Please try again."
            }
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = err)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            VoxoraAnalytics.logAuthGoogleFailed(appContext, e.javaClass.simpleName)
            VoxoraCrashlytics.recordException(e)
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)
        _voxoraAuthState.value = VoxoraAuthState.LOADING

        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val error = "Please enter a valid email address."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        if (password.length < 6) {
            val error = "Password must be at least 6 characters long."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        val firebaseAuth = VoxoraFirebaseService.getAuth()
        if (firebaseAuth != null) {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(trimmedEmail, password).await()
                val fbUser = authResult.user ?: throw IllegalStateException("Firebase returned empty user.")

                val user = AuthUser(
                    id = fbUser.uid,
                    name = fbUser.displayName ?: trimmedEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                    email = trimmedEmail,
                    username = "@${trimmedEmail.substringBefore("@").lowercase().replace(".", "_")}",
                    photoUrl = fbUser.photoUrl?.toString(),
                    country = prefs?.getString(KEY_USER_COUNTRY, "Malaysia") ?: "Malaysia",
                    isGuest = false,
                    learningLevel = prefs?.getString(KEY_USER_LEVEL, "Intermediate (Juz 5)") ?: "Intermediate (Juz 5)",
                    provider = "password"
                )

                persistSession(user, AuthMode.AUTHENTICATED)
                _currentUser.value = user
                _authMode.value = AuthMode.AUTHENTICATED
                _voxoraAuthState.value = VoxoraAuthState.SIGNED_IN
                _authUiState.value = _authUiState.value.copy(status = AuthStatus.SUCCESS, user = user)
                _authState.value = AuthState.MAIN_APP

                context?.let { VoxoraAnalytics.logAuthEmailSuccess(it) }
                VoxoraCrashlytics.setUserId(user.id)
                Result.success(user)
            } catch (e: Exception) {
                val msg = when (e) {
                    is com.google.firebase.FirebaseNetworkException -> "Network error: Please check your internet connection."
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "No account found with this email."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Incorrect email or password."
                    else -> e.localizedMessage ?: "Authentication failed."
                }
                _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = msg)
                _voxoraAuthState.value = VoxoraAuthState.ERROR
                VoxoraCrashlytics.recordException(e)
                Result.failure(e)
            }
        } else {
            val err = "Firebase Authentication is not configured yet. Please add your google-services.json from the Firebase Console."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = err)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            Result.failure(IllegalStateException(err))
        }
    }

    override suspend fun createAccount(
        name: String,
        email: String,
        password: String,
        country: String,
        level: String
    ): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)
        _voxoraAuthState.value = VoxoraAuthState.LOADING

        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank()) {
            val error = "Please enter your full name."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val error = "Please enter a valid email address."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        if (password.length < 6) {
            val error = "Password must be at least 6 characters long."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        val firebaseAuth = VoxoraFirebaseService.getAuth()
        if (firebaseAuth != null) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, password).await()
                val fbUser = authResult.user ?: throw IllegalStateException("Firebase returned empty user.")

                try {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(trimmedName)
                        .build()
                    fbUser.updateProfile(profileUpdates).await()
                } catch (_: Exception) {}

                val newUser = AuthUser(
                    id = fbUser.uid,
                    name = trimmedName,
                    email = trimmedEmail,
                    username = "@${trimmedEmail.substringBefore("@").lowercase().replace(".", "_")}",
                    photoUrl = null,
                    country = country.ifBlank { "Malaysia" },
                    isGuest = false,
                    learningLevel = level.ifBlank { "Beginner (Juz 1)" },
                    provider = "password"
                )

                persistSession(newUser, AuthMode.AUTHENTICATED)
                _currentUser.value = newUser
                _authMode.value = AuthMode.AUTHENTICATED
                _voxoraAuthState.value = VoxoraAuthState.SIGNED_IN
                _authUiState.value = _authUiState.value.copy(status = AuthStatus.SUCCESS, user = newUser)
                _authState.value = AuthState.MAIN_APP

                context?.let { VoxoraAnalytics.logAuthEmailSuccess(it) }
                VoxoraCrashlytics.setUserId(newUser.id)
                Result.success(newUser)
            } catch (e: Exception) {
                val msg = when (e) {
                    is com.google.firebase.FirebaseNetworkException -> "Network error: Please check your internet connection."
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "An account with this email already exists."
                    else -> e.localizedMessage ?: "Failed to create account."
                }
                _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = msg)
                _voxoraAuthState.value = VoxoraAuthState.ERROR
                VoxoraCrashlytics.recordException(e)
                Result.failure(e)
            }
        } else {
            val err = "Firebase Authentication is not configured yet. Please add your google-services.json from the Firebase Console."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = err)
            _voxoraAuthState.value = VoxoraAuthState.ERROR
            Result.failure(IllegalStateException(err))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)

        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val error = "Please enter a valid email address."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = error)
            return@withContext Result.failure(IllegalArgumentException(error))
        }

        val firebaseAuth = VoxoraFirebaseService.getAuth()
        if (firebaseAuth != null) {
            try {
                firebaseAuth.sendPasswordResetEmail(trimmedEmail).await()
                _authUiState.value = _authUiState.value.copy(status = AuthStatus.IDLE)
                Result.success(Unit)
            } catch (e: Exception) {
                val msg = when (e) {
                    is com.google.firebase.FirebaseNetworkException -> "Network error: Please check your internet connection."
                    else -> e.localizedMessage ?: "Failed to send reset email."
                }
                _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = msg)
                Result.failure(e)
            }
        } else {
            val err = "Firebase is not configured yet. Please add your google-services.json."
            _authUiState.value = _authUiState.value.copy(status = AuthStatus.FAILED, errorMessage = err)
            Result.failure(IllegalStateException(err))
        }
    }

    override suspend fun continueAsGuest(): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.LOADING, errorMessage = null)
        _voxoraAuthState.value = VoxoraAuthState.LOADING

        val guestUser = AuthUser(
            id = "guest_${System.currentTimeMillis() % 10000}",
            name = "Guest Learner",
            email = "guest@voxora.local",
            username = "@guest_learner",
            country = "Malaysia",
            isGuest = true,
            learningLevel = "Beginner (Juz 1)",
            provider = "guest"
        )

        persistSession(guestUser, AuthMode.GUEST)
        _currentUser.value = guestUser
        _authMode.value = AuthMode.GUEST
        _voxoraAuthState.value = VoxoraAuthState.GUEST
        _authUiState.value = _authUiState.value.copy(status = AuthStatus.SUCCESS, user = guestUser)
        _authState.value = AuthState.MAIN_APP

        Result.success(guestUser)
    }

    override suspend fun signOut(context: Context?): Result<Unit> = withContext(Dispatchers.IO) {
        val appContext = context ?: this@AuthRepository.context

        try {
            VoxoraFirebaseService.getAuth()?.signOut()
        } catch (_: Exception) {}

        if (appContext != null) {
            try {
                val credentialManager = CredentialManager.create(appContext)
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) {}
        }

        prefs?.edit()?.apply {
            putString(KEY_SESSION_MODE, AuthMode.UNAUTHENTICATED.name)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_USERNAME)
            remove(KEY_USER_PHOTO_URL)
            remove(KEY_USER_PROVIDER)
            apply()
        }

        _authMode.value = AuthMode.UNAUTHENTICATED
        _currentUser.value = null
        _voxoraAuthState.value = VoxoraAuthState.SIGNED_OUT
        _authState.value = AuthState.AUTH_ENTRY
        _authUiState.value = AuthUiState(status = AuthStatus.IDLE)

        appContext?.let { VoxoraAnalytics.logAuthSignOut(it) }
        Result.success(Unit)
    }

    private fun persistSession(user: AuthUser, mode: AuthMode) {
        prefs?.edit()?.apply {
            putString(KEY_SESSION_MODE, mode.name)
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_USERNAME, user.username)
            putString(KEY_USER_PHOTO_URL, user.photoUrl)
            putString(KEY_USER_COUNTRY, user.country)
            putString(KEY_USER_LEVEL, user.learningLevel)
            putBoolean(KEY_USER_IS_GUEST, user.isGuest)
            putString(KEY_USER_PROVIDER, user.provider)
            apply()
        }
    }
}
