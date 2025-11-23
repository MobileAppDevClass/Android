package com.example.teamproject.data.api

import com.google.gson.annotations.SerializedName

/**
 * Request model for signup API
 */
data class SignupRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("name")
    val name: String
)

/**
 * Response model for signup API
 */
data class SignupResponse(
    @SerializedName("username")
    val username: String,

    @SerializedName("name")
    val name: String
)

/**
 * Request model for login API
 */
data class LoginRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
)

/**
 * Response model for login API
 */
data class LoginResponse(
    @SerializedName("username")
    val username: String,

    @SerializedName("name")
    val name: String
)
