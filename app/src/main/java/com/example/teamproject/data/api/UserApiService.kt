package com.example.teamproject.data.api

import retrofit2.Response
import retrofit2.http.GET

/**
 * User API Service Interface
 */
interface UserApiService {

    /**
     * Get current user information
     * @return UserResponse with user details
     */
    @GET("users/me")
    suspend fun getCurrentUser(): Response<UserResponse>
}
