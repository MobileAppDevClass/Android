package com.example.teamproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.teamproject.viewmodel.AuthUiState
import com.example.teamproject.viewmodel.AuthViewModel

/**
 * Sample Signup Screen
 * This demonstrates how to use AuthViewModel and signup API
 */
@Composable
fun SignupScreen(
    authViewModel: AuthViewModel = viewModel(),
    onSignupSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val signupState by authViewModel.signupState.collectAsState()

    // Handle signup success
    LaunchedEffect(signupState) {
        if (signupState is AuthUiState.Success) {
            onSignupSuccess()
            authViewModel.resetSignupState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "회원가입",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            enabled = signupState !is AuthUiState.Loading
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = signupState !is AuthUiState.Loading
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true,
            enabled = signupState !is AuthUiState.Loading
        )

        Button(
            onClick = {
                if (username.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
                    authViewModel.signup(username, password, name)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = signupState !is AuthUiState.Loading &&
                    username.isNotBlank() &&
                    password.isNotBlank() &&
                    name.isNotBlank()
        ) {
            if (signupState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("회원가입")
            }
        }

        // Error message
        if (signupState is AuthUiState.Error) {
            Text(
                text = (signupState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Success message
        if (signupState is AuthUiState.Success) {
            val data = (signupState as AuthUiState.Success).data
            Text(
                text = "회원가입 성공! 환영합니다, ${data.name}님",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Navigate to login
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = onNavigateToLogin,
            enabled = signupState !is AuthUiState.Loading
        ) {
            Text("이미 계정이 있으신가요? 로그인")
        }
    }
}
