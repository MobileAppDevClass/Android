package com.example.teamproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamproject.data.api.UserResponse
import com.example.teamproject.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for User
 */
sealed class UserUiState {
    object Idle : UserUiState()
    object Loading : UserUiState()
    data class Success(val user: UserResponse) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

/**
 * ViewModel for user operations
 */
class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _userState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val userState: StateFlow<UserUiState> = _userState.asStateFlow()

    /**
     * Load current user information
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            _userState.value = UserUiState.Loading

            val result = repository.getCurrentUser()

            _userState.value = result.fold(
                onSuccess = { user ->
                    UserUiState.Success(user)
                },
                onFailure = { exception ->
                    UserUiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }

    /**
     * Reset user state to Idle
     */
    fun resetUserState() {
        _userState.value = UserUiState.Idle
    }
}
