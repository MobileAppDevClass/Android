package com.example.teamproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamproject.data.api.ActivityLevel
import com.example.teamproject.data.api.FriendsResponse
import com.example.teamproject.data.api.Gender
import com.example.teamproject.data.api.UpdateUserProfileRequest
import com.example.teamproject.data.api.UserProfileRequest
import com.example.teamproject.data.api.UserProfileResponse
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
 * UI State for User Profile
 */
sealed class UserProfileUiState {
    object Idle : UserProfileUiState()
    object Loading : UserProfileUiState()
    data class Success(val profile: UserProfileResponse) : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}

/**
 * UI State for Friends
 */
sealed class FriendsUiState {
    object Idle : FriendsUiState()
    object Loading : FriendsUiState()
    data class Success(val data: FriendsResponse) : FriendsUiState()
    data class Error(val message: String) : FriendsUiState()
}

/**
 * ViewModel for user operations
 */
class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _userState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val userState: StateFlow<UserUiState> = _userState.asStateFlow()

    private val _userProfileState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Idle)
    val userProfileState: StateFlow<UserProfileUiState> = _userProfileState.asStateFlow()

    private val _friendsState = MutableStateFlow<FriendsUiState>(FriendsUiState.Idle)
    val friendsState: StateFlow<FriendsUiState> = _friendsState.asStateFlow()

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
     * Create user profile with physical information
     * @param userId User ID
     * @param age User's age
     * @param gender User's gender
     * @param height User's height in cm
     * @param weight User's weight in kg
     * @param activityLevel User's activity level
     */
    fun createUserProfile(
        userId: Long,
        age: Int,
        gender: Gender,
        height: Double,
        weight: Double,
        activityLevel: ActivityLevel
    ) {
        viewModelScope.launch {
            _userProfileState.value = UserProfileUiState.Loading

            val request = UserProfileRequest(
                userId = userId,
                age = age,
                gender = gender,
                height = height,
                weight = weight,
                activityLevel = activityLevel
            )

            val result = repository.createUserProfile(request)

            _userProfileState.value = result.fold(
                onSuccess = { profile ->
                    UserProfileUiState.Success(profile)
                },
                onFailure = { exception ->
                    UserProfileUiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }

    /**
     * Update user profile with physical information
     * @param profileId Profile ID
     * @param age User's age
     * @param gender User's gender
     * @param height User's height in cm
     * @param weight User's weight in kg
     * @param activityLevel User's activity level
     */
    fun updateUserProfile(
        profileId: Long,
        age: Int,
        gender: Gender,
        height: Double,
        weight: Double,
        activityLevel: ActivityLevel
    ) {
        viewModelScope.launch {
            _userProfileState.value = UserProfileUiState.Loading

            val request = UpdateUserProfileRequest(
                age = age,
                gender = gender,
                height = height,
                weight = weight,
                activityLevel = activityLevel
            )

            val result = repository.updateUserProfile(profileId, request)

            _userProfileState.value = result.fold(
                onSuccess = { profile ->
                    UserProfileUiState.Success(profile)
                },
                onFailure = { exception ->
                    UserProfileUiState.Error(exception.message ?: "Unknown error occurred")
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

    /**
     * Reset user profile state to Idle
     */
    fun resetUserProfileState() {
        _userProfileState.value = UserProfileUiState.Idle
    }

    /**
     * Load friends list
     */
    fun loadFriends() {
        viewModelScope.launch {
            _friendsState.value = FriendsUiState.Loading

            val result = repository.getFriends()

            _friendsState.value = result.fold(
                onSuccess = { data ->
                    FriendsUiState.Success(data)
                },
                onFailure = { exception ->
                    FriendsUiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }

    /**
     * Reset friends state to Idle
     */
    fun resetFriendsState() {
        _friendsState.value = FriendsUiState.Idle
    }
}
