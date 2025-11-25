package com.example.teamproject.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Drink API Service Interface
 */
interface DrinkApiService {

    /**
     * Get drink records with optional filters
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param startDate Start date in ISO format (optional)
     * @param endDate End date in ISO format (optional)
     * @param userId User ID (optional, defaults to current user)
     * @return DrinkRecordsResponse with paginated records
     */
    @GET("drink-records")
    suspend fun getDrinkRecords(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("userId") userId: Long? = null
    ): Response<DrinkRecordsResponse>
}
