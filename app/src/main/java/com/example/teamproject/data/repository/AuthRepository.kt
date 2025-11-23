package com.example.teamproject.data.repository

import com.example.teamproject.data.api.AuthApiService
import com.example.teamproject.data.api.LoginRequest
import com.example.teamproject.data.api.LoginResponse
import com.example.teamproject.data.api.RetrofitClient
import com.example.teamproject.data.api.SignupRequest
import com.example.teamproject.data.api.SignupResponse

/**
 * Repository for authentication operations
 */
class AuthRepository(
    private val authApiService: AuthApiService = RetrofitClient.authApiService
) {

    /**
     * Sign up a new user
     * @param username User's username
     * @param password User's password
     * @param name User's name
     * @return Result containing SignupResponse or error
     */
    suspend fun signup(
        username: String,
        password: String,
        name: String
    ): Result<SignupResponse> {
        return try {
            val request = SignupRequest(
                username = username,
                password = password,
                name = name
            )

            val response = authApiService.signup(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Signup failed: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Log in a user
     * @param username User's username
     * @param password User's password
     * @return Result containing LoginResponse or error
     */
    suspend fun login(
        username: String,
        password: String
    ): Result<LoginResponse> {
        return try {
            val request = LoginRequest(
                username = username,
                password = password
            )

            val response = authApiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Login failed: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
