package com.kigz.javillasafarihub.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.UserProfileChangeRequest
import androidx.compose.ui.Modifier
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kigz.javillasafarihub.ui.theme.SavannahGold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {

    var fullName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = SavannahGold,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join SafariHub and start exploring Kenya.",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Full Name", fontSize = 14.sp)
            },
            placeholder = { Text("John Doe", color = SavannahGold.copy(alpha = 0.5f), fontSize = 14.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = SavannahGold,
                unfocusedLabelColor = SavannahGold.copy(alpha = 0.8f),
                focusedPlaceholderColor = SavannahGold,
                unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Email", fontSize = 14.sp)
            },
            placeholder = { Text("example@gmail.com", color = SavannahGold.copy(alpha = 0.5f), fontSize = 14.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = SavannahGold,
                unfocusedLabelColor = SavannahGold.copy(alpha = 0.8f),
                focusedPlaceholderColor = SavannahGold,
                unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Password", fontSize = 14.sp)
            },
            placeholder = { Text("At least 6 characters", color = SavannahGold.copy(alpha = 0.5f), fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = SavannahGold,
                unfocusedLabelColor = SavannahGold.copy(alpha = 0.8f),
                focusedPlaceholderColor = SavannahGold,
                unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Confirm Password", fontSize = 14.sp)
            },
            placeholder = { Text("Re-type password", color = SavannahGold.copy(alpha = 0.5f), fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = SavannahGold,
                unfocusedLabelColor = SavannahGold.copy(alpha = 0.8f),
                focusedPlaceholderColor = SavannahGold,
                unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
            )
        )

        if (errorMessage.isNotEmpty()) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            enabled = !isLoading,
            onClick = {

                when {
                    fullName.isBlank() -> {
                        errorMessage = "Please enter your name."
                    }

                    email.isBlank() -> {
                        errorMessage = "Please enter your email."
                    }

                    password.length < 6 -> {
                        errorMessage =
                            "Password must contain at least 6 characters."
                    }

                    password != confirmPassword -> {
                        errorMessage =
                            "Passwords do not match."
                    }

                    else -> {
                        isLoading = true
                        errorMessage = ""
                        val auth = FirebaseAuth.getInstance()
                        auth.createUserWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    isLoading = false
                                    errorMessage = task.exception?.localizedMessage
                                        ?: "Account creation failed. Please try again."
                                    return@addOnCompleteListener
                                }

                                val profileUpdate = UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName.trim())
                                    .build()

                                auth.currentUser?.updateProfile(profileUpdate)
                                    ?.addOnCompleteListener { profileTask ->
                                        isLoading = false
                                        if (profileTask.isSuccessful) {
                                            val user = auth.currentUser
                                            user?.uid?.let { uid ->
                                                FirebaseDatabase.getInstance().reference
                                                    .child("users").child(uid)
                                                    .updateChildren(
                                                        mapOf(
                                                            "displayName" to fullName.trim(),
                                                            "email" to email.trim(),
                                                            "updatedAt" to System.currentTimeMillis()
                                                        )
                                                    )
                                            }
                                            onRegisterSuccess()
                                        } else {
                                            errorMessage = profileTask.exception?.localizedMessage
                                                ?: "Account created, but profile setup failed."
                                        }
                                    }
                                    ?: run {
                                        isLoading = false
                                        errorMessage = "Account creation failed. Please try again."
                                    }
                            }
                    }
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Creating account..." else "Create Account")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have an account? Login")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onRegisterSuccess = {},
        onLoginClick = {}
    )
}