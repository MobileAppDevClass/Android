package com.example.teamproject.data.repository

import com.example.teamproject.data.api.CreateDrinkRecordRequest
import com.example.teamproject.data.api.DrinkApiService
import com.example.teamproject.data.api.DrinkRecord
import com.example.teamproject.data.api.DrinkRecordsResponse
import com.example.teamproject.data.api.RetrofitClient

/**
 * Repository for drink record operations
 */
class DrinkRepository(
    private val drinkApiService: DrinkApiService = RetrofitClient.drinkApiService
) {

    /**
     * Get drink records with optional filters
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param startDate Start date in ISO format (optional)
     * @param endDate End date in ISO format (optional)
     * @param userId User ID (optional, defaults to current user)
     * @return Result containing DrinkRecordsResponse or error
     */
    suspend fun getDrinkRecords(
        page: Int = 0,
        size: Int = 20,
        startDate: String? = null,
        endDate: String? = null,
        userId: Long? = null
    ): Result<DrinkRecordsResponse> {
        return try {
            val response = drinkApiService.getDrinkRecords(
                page = page,
                size = size,
                startDate = startDate,
                endDate = endDate,
                userId = userId
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to get drink records: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create a new drink record
     * @param amount Amount of water in ml
     * @return Result containing DrinkRecord or error
     */
    suspend fun createDrinkRecord(amount: Int): Result<DrinkRecord> {
        return try {
            val request = CreateDrinkRecordRequest(amount = amount)
            val response = drinkApiService.createDrinkRecord(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Failed to create drink record: ${response.code()} ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
