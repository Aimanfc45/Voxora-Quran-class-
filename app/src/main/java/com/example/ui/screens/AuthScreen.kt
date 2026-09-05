package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthFormType
import com.example.data.model.AuthStatus
import com.example.data.repository.AuthRepository
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    repository: VoxoraRepository,
    authRepository: AuthRepository,
    onAuthSuccess: () -> Unit,
    onBack: (() -> Unit)? = null,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authUiState by authRepository.authUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var activeForm by remember { mutableStateOf(AuthFormType.WELCOME) }

    // Form inputs
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var countryInput by remember { mutableStateOf("Malaysia") }
    var learningLevelInput by remember { mutableStateOf("Intermediate (Juz 5)") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    val isLoading = authUiState.status == AuthStatus.LOADING

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        color = DeepEmerald950
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SubtleIslamicPattern(
                modifier = Modifier.fillMaxSize(),
                patternColor = GoldPrimary.copy(alpha = 0.05f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (activeForm != AuthFormType.WELCOME) {
                        IconButton(
                            onClick = {
                                activeForm = AuthFormType.WELCOME
                                localErrorMessage = null
                                authRepository.resetUiState()
                            },
                            modifier = Modifier.testTag("auth_back_to_welcome_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Gold400
                            )
                        }
                    } else if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("auth_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Gold400
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "VOXORA",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 3.sp,
                                color = Gold400
                            )
                        )
                        Text(
                            text = "Muslim Centre",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp,
                                color = Emerald200.copy(alpha = 0.8f)
                            )
                        )
                    }

                    // Top quick guest shortcut
                    TextButton(
                        onClick = {
                            scope.launch {
                                val result = authRepository.continueAsGuest()
                                if (result.isSuccess) {
                                    val guestUser = result.getOrNull()
                                    if (guestUser != null) {
                                        repository.syncWithAuthUser(guestUser)
                                    } else {
                                        repository.continueAsGuestUser()
                                    }
                                    onShowSnackbar("Entered Guest Mode. Full Quran & Prayer tools available.")
                                    onAuthSuccess()
                                }
                            }
                        },
                        modifier = Modifier.testTag("auth_top_guest_button")
                    ) {
                        Text(
                            text = "Guest",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald200
                            )
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
                ) {
                    // Header Brand & Title
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        ) {
                            // Emblem
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Emerald800, DeepEmerald950)
                                        )
                                    )
                                    .border(2.dp, Gold500, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📖", fontSize = 36.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "VOXORA",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 4.sp,
                                    color = Color.White
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("auth_welcome_title")
                            )

                            Text(
                                text = "Muslim Centre",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = Gold400
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Learn. Recite. Grow.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Emerald200.copy(alpha = 0.9f),
                                    letterSpacing = 0.5.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("auth_welcome_subtitle")
                            )
                        }
                    }

                    // Error banner if any
                    val displayedError = localErrorMessage ?: authUiState.errorMessage
                    if (displayedError != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF7F1D1D).copy(alpha = 0.8f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFFCA5A5),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = displayedError,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // MAIN WELCOME VIEW (Buttons: Continue with Google, Sign In, Create Account, OR, Continue as Guest)
                    if (activeForm == AuthFormType.WELCOME) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // 1. Continue with Google Button
                                Button(
                                    onClick = {
                                        if (isLoading) return@Button
                                        scope.launch {
                                            localErrorMessage = null
                                            val result = authRepository.signInWithGoogle(context)
                                            if (result.isSuccess) {
                                                val user = result.getOrNull()
                                                if (user != null) {
                                                    repository.syncWithAuthUser(user)
                                                    onShowSnackbar("Signed in with Google as ${user.name}!")
                                                    onAuthSuccess()
                                                }
                                            } else {
                                                localErrorMessage = result.exceptionOrNull()?.message ?: "Google Sign-In failed."
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("auth_google_button"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF1F2937)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Emerald800,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            GoogleLogoIcon(
                                                size = 22.dp,
                                                modifier = Modifier.padding(end = 12.dp)
                                            )
                                            Text(
                                                text = "Continue with Google",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1F2937)
                                                )
                                            )
                                        }
                                    }
                                }

                                // 2. Sign In Button
                                Button(
                                    onClick = {
                                        activeForm = AuthFormType.SIGN_IN
                                        localErrorMessage = null
                                        authRepository.resetUiState()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("auth_sign_in_entry_button"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Gold500,
                                        contentColor = DeepEmerald950
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Login,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Sign In with Email",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                // 3. Create Account Button
                                OutlinedButton(
                                    onClick = {
                                        activeForm = AuthFormType.CREATE_ACCOUNT
                                        localErrorMessage = null
                                        authRepository.resetUiState()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("auth_create_account_entry_button"),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold400),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Gold400
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PersonAdd,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Create Account",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                // Separator: OR
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = Emerald700.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = "OR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald300
                                        ),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = Emerald700.copy(alpha = 0.5f)
                                    )
                                }

                                // 4. Continue as Guest Button
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Emerald900.copy(alpha = 0.6f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Emerald700.copy(alpha = 0.7f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isLoading) return@clickable
                                            scope.launch {
                                                val result = authRepository.continueAsGuest()
                                                if (result.isSuccess) {
                                                    val guestUser = result.getOrNull()
                                                    if (guestUser != null) {
                                                        repository.syncWithAuthUser(guestUser)
                                                    } else {
                                                        repository.continueAsGuestUser()
                                                    }
                                                    onShowSnackbar("Entered Guest Mode. Enjoy full Quran recitation & features!")
                                                    onAuthSuccess()
                                                }
                                            }
                                        }
                                        .testTag("auth_continue_as_guest_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(Emerald800),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AccountCircle,
                                                contentDescription = null,
                                                tint = Gold400,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Continue as Guest",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                            Text(
                                                text = "Instant access to Quran, Audio, Tajwid & Prayer Times",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Emerald200.copy(alpha = 0.8f),
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = Gold400
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // EMAIL SIGN IN / CREATE ACCOUNT FORMS
                    if (activeForm == AuthFormType.SIGN_IN || activeForm == AuthFormType.CREATE_ACCOUNT) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Form Title
                                    Text(
                                        text = if (activeForm == AuthFormType.SIGN_IN) "Sign In with Email" else "Create Voxora Account",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )

                                    if (activeForm == AuthFormType.CREATE_ACCOUNT) {
                                        OutlinedTextField(
                                            value = nameInput,
                                            onValueChange = { nameInput = it },
                                            label = { Text("Full Name") },
                                            placeholder = { Text("e.g. Ahmed Al-Farsi") },
                                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("auth_name_input"),
                                            shape = RoundedCornerShape(14.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = countryInput,
                                            onValueChange = { countryInput = it },
                                            label = { Text("Country") },
                                            leadingIcon = { Icon(Icons.Default.Public, contentDescription = null) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("auth_country_input"),
                                            shape = RoundedCornerShape(14.dp),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = learningLevelInput,
                                            onValueChange = { learningLevelInput = it },
                                            label = { Text("Target Learning Level") },
                                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("auth_level_input"),
                                            shape = RoundedCornerShape(14.dp),
                                            singleLine = true
                                        )
                                    }

                                    // Email Field
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { emailInput = it },
                                        label = { Text("Email Address") },
                                        placeholder = { Text("learner@voxora.app") },
                                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Email,
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("auth_email_input"),
                                        shape = RoundedCornerShape(14.dp),
                                        singleLine = true
                                    )

                                    // Password Field
                                    OutlinedTextField(
                                        value = passwordInput,
                                        onValueChange = { passwordInput = it },
                                        label = { Text("Password") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                                )
                                            }
                                        },
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { focusManager.clearFocus() }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("auth_password_input"),
                                        shape = RoundedCornerShape(14.dp),
                                        singleLine = true
                                    )

                                    // Submit Button
                                    Button(
                                        onClick = {
                                            if (isLoading) return@Button
                                            focusManager.clearFocus()
                                            scope.launch {
                                                localErrorMessage = null
                                                if (activeForm == AuthFormType.SIGN_IN) {
                                                    val result = authRepository.signInWithEmail(emailInput, passwordInput)
                                                    if (result.isSuccess) {
                                                        val user = result.getOrNull()!!
                                                        repository.syncWithAuthUser(user)
                                                        onShowSnackbar("Signed in successfully as ${user.name}!")
                                                        onAuthSuccess()
                                                    } else {
                                                        localErrorMessage = result.exceptionOrNull()?.message ?: "Sign In failed."
                                                    }
                                                } else {
                                                    val result = authRepository.createAccount(
                                                        name = nameInput,
                                                        email = emailInput,
                                                        password = passwordInput,
                                                        country = countryInput,
                                                        level = learningLevelInput
                                                    )
                                                    if (result.isSuccess) {
                                                        val user = result.getOrNull()!!
                                                        repository.syncWithAuthUser(user)
                                                        onShowSnackbar("Account created! Welcome to Voxora.")
                                                        onAuthSuccess()
                                                    } else {
                                                        localErrorMessage = result.exceptionOrNull()?.message ?: "Account creation failed."
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("auth_submit_button"),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Emerald800,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(22.dp),
                                                color = Color.White,
                                                strokeWidth = 2.5.dp
                                            )
                                        } else {
                                            Text(
                                                text = if (activeForm == AuthFormType.SIGN_IN) "Sign In" else "Create Account",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }

                                    // Switch between Sign In and Create Account
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (activeForm == AuthFormType.SIGN_IN) "Don't have an account? " else "Already have an account? ",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                        Text(
                                            text = if (activeForm == AuthFormType.SIGN_IN) "Create one" else "Sign in",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Emerald700
                                            ),
                                            modifier = Modifier.clickable {
                                                activeForm = if (activeForm == AuthFormType.SIGN_IN) AuthFormType.CREATE_ACCOUNT else AuthFormType.SIGN_IN
                                                localErrorMessage = null
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
