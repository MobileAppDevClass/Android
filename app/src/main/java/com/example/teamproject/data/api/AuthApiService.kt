package com.example.teamproject.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Auth API Service Interface
 */
interface AuthApiService {

    /**
     * Signup API
     * @param request SignupRequest containing username, password, and name
     * @return SignupResponse with username and name
     */
    @POST("auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    /**
     * Login API
     * @param request LoginRequest containing username and password
     * @return LoginResponse with username and name
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    /**
     * Logout API
     * @param cookie Cookie header containing refreshToken
     * @return Empty response
     */
    @POST("auth/logout")
    suspend fun logout(
        @Header("Cookie") cookie: String = ""
    ): Response<Unit>
}
