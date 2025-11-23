package com.example.teamproject.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages JWT tokens using SharedPreferences
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"

        @Volatile
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Save access token
     */
    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    /**
     * Get access token
     */
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Clear all tokens
     */
    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }
}
