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
 * Login Screen
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onNavigateToSignup: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState.collectAsState()

    // Handle login success
    LaunchedEffect(loginState) {
        if (loginState is AuthUiState.Success) {
            onLoginSuccess()
            authViewModel.resetLoginState()
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
            text = "로그인",
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
            enabled = loginState !is AuthUiState.Loading
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = loginState !is AuthUiState.Loading
        )

        Button(
            onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    authViewModel.login(username, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = loginState !is AuthUiState.Loading &&
                    username.isNotBlank() &&
                    password.isNotBlank()
        ) {
            if (loginState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("로그인")
            }
        }

        // Error message
        if (loginState is AuthUiState.Error) {
            Text(
                text = (loginState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Success message
        if (loginState is AuthUiState.Success) {
            val data = (loginState as AuthUiState.Success).data
            Text(
                text = "로그인 성공! 환영합니다, ${data.name}님",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Navigate to signup
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = onNavigateToSignup,
            enabled = loginState !is AuthUiState.Loading
        ) {
            Text("계정이 없으신가요? 회원가입")
        }
    }
}
