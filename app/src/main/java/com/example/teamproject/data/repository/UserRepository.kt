package com.example.teamproject.data.repository

import com.example.teamproject.data.api.FriendsResponse
import com.example.teamproject.data.api.RetrofitClient
import com.example.teamproject.data.api.UserApiService
import com.example.teamproject.data.api.UserProfileRequest
import com.example.teamproject.data.api.UserProfileResponse
import com.example.teamproject.data.api.UserResponse

/**
 * Repository for user operations
 */
class UserRepository(
    private val userApiService: UserApiService = RetrofitClient.userApiService
) {

    /**
     * Get current user information
     * @return Result containing UserResponse or error
     */
    suspend fun getCurrentUser(): Result<UserResponse> {
        return try {
            val response = userApiService.getCurrentUser()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to get user: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create user profile with physical information
     * @param request UserProfileRequest containing user's physical data
     * @return Result containing UserProfileResponse or error
     */
    suspend fun createUserProfile(request: UserProfileRequest): Result<UserProfileResponse> {
        return try {
            val response = userApiService.createUserProfile(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to create user profile: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get friends list
     * @return Result containing FriendsResponse or error
     */
    suspend fun getFriends(): Result<FriendsResponse> {
        return try {
            val response = userApiService.getFriends()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to get friends: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
