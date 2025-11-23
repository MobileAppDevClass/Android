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
    val profile: String?
)
