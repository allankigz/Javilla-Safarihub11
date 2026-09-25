package com.kigz.javillasafarihub.auth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.kigz.javillasafarihub.ui.theme.JavillaSafariHubTheme
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
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
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = SavannahGold,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue exploring Kenya.",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

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
            placeholder = { Text("Enter your password", color = SavannahGold.copy(alpha = 0.5f), fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {

                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {

                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = SavannahGold,
                unfocusedLabelColor = SavannahGold.copy(alpha = 0.8f),
                focusedPlaceholderColor = SavannahGold,
                unfocusedPlaceholderColor = SavannahGold.copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onForgotPasswordClick
        ) {
            Text("Forgot Password?")
        }

        if (errorMessage.isNotEmpty()) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = !isLoading,
            onClick = {

                when {
                    email.isBlank() -> {
                        errorMessage = "Please enter your email."
                    }

                    password.isBlank() -> {
                        errorMessage = "Please enter your password."
                    }

                    else -> {
                        isLoading = true
                        errorMessage = ""
                        FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = task.exception?.localizedMessage
                                        ?: "Login failed. Please check your email and password."
                                }
                            }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Signing in..." else "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
    }
}
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LoginScreenPreview() {
    JavillaSafariHubTheme {
        LoginScreen(
            onLoginSuccess = {},
            onRegisterClick = {},
            onForgotPasswordClick = {}
        )
    }
}