package com.example.teamproject.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

    /**
     * Create user profile with physical information
     * @param request UserProfileRequest containing age, gender, height, weight, and activity level
     * @return UserProfileResponse with created profile details
     */
    @POST("user-profiles")
    suspend fun createUserProfile(
        @Body request: UserProfileRequest
    ): Response<UserProfileResponse>
}
