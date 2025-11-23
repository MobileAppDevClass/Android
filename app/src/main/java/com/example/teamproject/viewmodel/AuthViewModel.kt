package com.example.teamproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamproject.data.api.LoginResponse
import com.example.teamproject.data.api.SignupResponse
import com.example.teamproject.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Common Auth Response Data
 */
data class AuthResponseData(
    val username: String,
    val name: String
)

/**
 * UI State for Authentication
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val data: AuthResponseData) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/**
 * ViewModel for authentication operations
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _signupState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val signupState: StateFlow<AuthUiState> = _signupState.asStateFlow()

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    /**
     * Sign up a new user
     */
    fun signup(username: String, password: String, name: String) {
        viewModelScope.launch {
            _signupState.value = AuthUiState.Loading

            val result = repository.signup(username, password, name)

            _signupState.value = result.fold(
                onSuccess = { response ->
                    AuthUiState.Success(
                        AuthResponseData(
                            username = response.username,
                            name = response.name
                        )
                    )
                },
                onFailure = { exception ->
                    AuthUiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }

    /**
     * Log in a user
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthUiState.Loading

            val result = repository.login(username, password)

            _loginState.value = result.fold(
                onSuccess = { response ->
                    AuthUiState.Success(
                        AuthResponseData(
                            username = response.username,
                            name = response.name
                        )
                    )
                },
                onFailure = { exception ->
                    AuthUiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }

    /**
     * Reset signup state to Idle
     */
    fun resetSignupState() {
        _signupState.value = AuthUiState.Idle
    }

    /**
     * Reset login state to Idle
     */
    fun resetLoginState() {
        _loginState.value = AuthUiState.Idle
    }
}
