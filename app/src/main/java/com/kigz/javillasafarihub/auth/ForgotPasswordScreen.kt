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
import androidx.compose.ui.Modifier
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kigz.javillasafarihub.ui.theme.SavannahGold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit
) {
    var email by remember {
        mutableStateOf("")
    }

    var message by remember {
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
            text = "Forgot Password?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = SavannahGold,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your email to reset your password.",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                message = ""
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

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            enabled = !isLoading,
            onClick = {
                if (email.isBlank()) {
                    message = "Please enter your email."
                } else {
                    isLoading = true
                    message = ""
                    FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(email.trim())
                        .addOnCompleteListener { task ->
                            isLoading = false
                            message = if (task.isSuccessful) {
                                "Password reset instructions have been sent to your email."
                            } else {
                                task.exception?.localizedMessage
                                    ?: "Unable to send password reset email. Please try again."
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Sending..." else "Reset Password")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ForgotPasswordScreenPreview() {
    JavillaSafariHubTheme {
        ForgotPasswordScreen(
            onBackToLogin = {}
        )
    }
}