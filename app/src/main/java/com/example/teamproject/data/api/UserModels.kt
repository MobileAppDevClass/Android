package com.example.teamproject.data.api

import com.google.gson.annotations.SerializedName

/**
 * User response model
 */
data class UserResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("profile")
    val profile: UserProfileResponse?
)

/**
 * Gender enum
 */
enum class Gender {
    @SerializedName("MALE")
    MALE,

    @SerializedName("FEMALE")
    FEMALE
}

/**
 * Activity level enum
 */
enum class ActivityLevel {
    @SerializedName("LOW")
    LOW,

    @SerializedName("MEDIUM")
    MEDIUM,

    @SerializedName("HIGH")
    HIGH
}

/**
 * Request model for creating user profile
 */
data class UserProfileRequest(
    @SerializedName("userId")
    val userId: Long,

    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: Gender,

    @SerializedName("height")
    val height: Double,

    @SerializedName("weight")
    val weight: Double,

    @SerializedName("activityLevel")
    val activityLevel: ActivityLevel
)

/**
 * Request model for updating user profile
 */
data class UpdateUserProfileRequest(
    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: Gender,

    @SerializedName("height")
    val height: Double,

    @SerializedName("weight")
    val weight: Double,

    @SerializedName("activityLevel")
    val activityLevel: ActivityLevel
)

/**
 * Response model for user profile
 */
data class UserProfileResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: Gender,

    @SerializedName("height")
    val height: Double,

    @SerializedName("weight")
    val weight: Double,

    @SerializedName("activityLevel")
    val activityLevel: ActivityLevel,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("todayAmount")
    val todayAmount: Int,

    @SerializedName("recommendAmount")
    val recommendAmount: Int
)

/**
 * Friend model
 */
data class FriendInfo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("username")
    val username: String
)

/**
 * Response model for friends list
 */
data class FriendsResponse(
    @SerializedName("friends")
    val friends: List<FriendInfo>
)

/**
 * Ranking info model
 */
data class RankingInfo(
    @SerializedName("userId")
    val userId: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("totalAmount")
    val totalAmount: Int,

    @SerializedName("rank")
    val rank: Int
)

/**
 * Response model for today's rankings
 */
data class RankingsResponse(
    @SerializedName("rankings")
    val rankings: List<RankingInfo>
)
