package com.example.teamproject.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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

    /**
     * Update user profile with physical information
     * @param profileId Profile ID
     * @param request UpdateUserProfileRequest containing age, gender, height, weight, and activity level
     * @return UserProfileResponse with updated profile details
     */
    @PATCH("user-profiles/{profileId}")
    suspend fun updateUserProfile(
        @Path("profileId") profileId: Long,
        @Body request: UpdateUserProfileRequest
    ): Response<UserProfileResponse>

    /**
     * Get friends list
     * @return FriendsResponse with list of friends
     */
    @GET("friends")
    suspend fun getFriends(): Response<FriendsResponse>
}
