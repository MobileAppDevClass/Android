package com.example.teamproject.data.api

import com.google.gson.annotations.SerializedName

/**
 * Drink record model
 */
data class DrinkRecord(
    @SerializedName("id")
    val id: Long,

    @SerializedName("amount")
    val amount: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("userId")
    val userId: Long
)

/**
 * Response model for drink records with pagination
 */
data class DrinkRecordsResponse(
    @SerializedName("records")
    val records: List<DrinkRecord>,

    @SerializedName("page")
    val page: Int,

    @SerializedName("size")
    val size: Int,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalElements")
    val totalElements: Int
)
