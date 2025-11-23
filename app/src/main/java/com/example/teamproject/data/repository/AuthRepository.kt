package com.example.teamproject.data.repository

import com.example.teamproject.TeamProjectApplication
import com.example.teamproject.data.TokenManager
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
    private val authApiService: AuthApiService = RetrofitClient.authApiService,
    private val tokenManager: TokenManager = TokenManager.getInstance(TeamProjectApplication.instance)
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
                // Extract and save token from response header
                val authHeader = response.headers()["Authorization"]
                authHeader?.let { header ->
                    // Extract token from "Bearer <token>" format
                    val token = header.removePrefix("Bearer ").trim()
                    if (token.isNotEmpty()) {
                        tokenManager.saveAccessToken(token)
                    }
                }

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

    /**
     * Log out the current user
     * @return Result indicating success or error
     */
    suspend fun logout(): Result<Unit> {
        return try {
            val response = authApiService.logout()

            // Clear tokens regardless of API response
            tokenManager.clearTokens()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                // Even if API call fails, we cleared local tokens
                Result.success(Unit)
            }
        } catch (e: Exception) {
            // Clear tokens even on error
            tokenManager.clearTokens()
            Result.success(Unit)
        }
    }
}
