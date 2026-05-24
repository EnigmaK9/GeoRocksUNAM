package com.enigma.georocks.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("georocks_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USERNAME = "username"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_JWT_TOKEN, null)
    }

    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
}
