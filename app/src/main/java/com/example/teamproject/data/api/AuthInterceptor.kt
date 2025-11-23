package com.example.teamproject.data.api

import com.example.teamproject.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add Authorization header to all requests
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding token for auth endpoints
        val isAuthEndpoint = originalRequest.url.encodedPath.contains("/auth/login") ||
                originalRequest.url.encodedPath.contains("/auth/signup")

        // If it's an auth endpoint or no token available, proceed with original request
        if (isAuthEndpoint) {
            return chain.proceed(originalRequest)
        }

        // Add token to request if available
        val token = tokenManager.getAccessToken()
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
