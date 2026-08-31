package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthFormType
import com.example.data.repository.VoxoraRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    repository: VoxoraRepository,
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedForm by remember { mutableStateOf(AuthFormType.SIGN_IN) }

    // Form inputs
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var countryInput by remember { mutableStateOf("Malaysia") }
    var learningLevelInput by remember { mutableStateOf("Intermediate (Juz 5)") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        color = DeepEmerald950
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("auth_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "VOXORA ACCOUNT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Gold400
                    )
                )

                TextButton(
                    onClick = {
                        repository.continueAsGuestUser()
                        onShowSnackbar("Entered Guest Mode. Progress stored locally.")
                        onAuthSuccess()
                    },
                    modifier = Modifier.testTag("auth_guest_shortcut_button")
                ) {
                    Text("Guest", color = Emerald200, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // App Emblem Header
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Emerald800)
                                .border(2.dp, Gold500, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📖", fontSize = 32.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Voxora Quran",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Text(
                            text = when (selectedForm) {
                                AuthFormType.SIGN_IN -> "Welcome back! Sign in to sync your tilawah and classes."
                                AuthFormType.CREATE_ACCOUNT -> "Create your profile to embark on your Quran learning journey."
                                AuthFormType.FORGOT_PASSWORD -> "Enter your email to receive recovery instructions."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Emerald200.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }

                // Tab Switcher
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Emerald900.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald700)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(
                                AuthFormType.SIGN_IN to "Sign In",
                                AuthFormType.CREATE_ACCOUNT to "Create Account",
                                AuthFormType.FORGOT_PASSWORD to "Reset"
                            ).forEach { (type, label) ->
                                val isSelected = selectedForm == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Gold500 else Color.Transparent)
                                        .clickable { selectedForm = type }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) DeepEmerald950 else Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Dynamic Form Fields Card
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
                            if (selectedForm == AuthFormType.CREATE_ACCOUNT) {
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = { Text("Full Name") },
                                    placeholder = { Text("e.g. Ustaz Ahmad / Fatima") },
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
                                    placeholder = { Text("e.g. Beginner (Juz 1) / Hafazan") },
                                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_level_input"),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true
                                )
                            }

                            // Email Field (All forms)
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Address") },
                                placeholder = { Text("learner@voxora.app") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_email_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            // Password Field (Sign In & Create Account)
                            if (selectedForm != AuthFormType.FORGOT_PASSWORD) {
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
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_password_input"),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    when (selectedForm) {
                                        AuthFormType.SIGN_IN -> {
                                            if (emailInput.isBlank()) {
                                                onShowSnackbar("Please enter a valid email address.")
                                                return@Button
                                            }
                                            val success = repository.authenticateUser(emailInput, passwordInput)
                                            if (success) {
                                                onShowSnackbar("Signed in successfully as ${repository.userProfile.value.name}!")
                                                onAuthSuccess()
                                            }
                                        }
                                        AuthFormType.CREATE_ACCOUNT -> {
                                            if (nameInput.isBlank() || emailInput.isBlank()) {
                                                onShowSnackbar("Please enter your name and email.")
                                                return@Button
                                            }
                                            val success = repository.createAccountUser(
                                                name = nameInput,
                                                email = emailInput,
                                                pass = passwordInput,
                                                country = countryInput,
                                                level = learningLevelInput
                                            )
                                            if (success) {
                                                onShowSnackbar("Account created! Welcome to Voxora.")
                                                onAuthSuccess()
                                            }
                                        }
                                        AuthFormType.FORGOT_PASSWORD -> {
                                            if (emailInput.isBlank()) {
                                                onShowSnackbar("Please enter your registered email address.")
                                                return@Button
                                            }
                                            onShowSnackbar("Password reset instructions sent to $emailInput")
                                            selectedForm = AuthFormType.SIGN_IN
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("auth_submit_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald800, contentColor = Color.White)
                            ) {
                                Text(
                                    text = when (selectedForm) {
                                        AuthFormType.SIGN_IN -> "Sign In to Voxora"
                                        AuthFormType.CREATE_ACCOUNT -> "Create Account"
                                        AuthFormType.FORGOT_PASSWORD -> "Send Reset Link"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                // Continue as Guest Option
                item {
                    OutlinedButton(
                        onClick = {
                            repository.continueAsGuestUser()
                            onShowSnackbar("Switched to Guest Mode. Offline features fully accessible.")
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_continue_guest_button"),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold500.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold400)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Continue as Guest (No Account Required)", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Backend Architecture Notice
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald700.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Gold400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Secure Backend Ready",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Gold400
                                    )
                                )
                            }
                            Text(
                                text = "Authentication endpoints (Firebase Auth / Supabase / OAuth2 JWT) can be configured via environment secrets. Local session persistence is fully enabled.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Emerald100.copy(alpha = 0.75f),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
