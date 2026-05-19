package com.example.neatify.utils

import android.content.Context

class SessionManager(context: Context) {

    private val pref = context.getSharedPreferences("NEATIFY_SESSION", Context.MODE_PRIVATE)

    fun saveLogin(userId: Int, name: String, token: String, role: String?) {
        pref.edit()
            .putBoolean("is_login", true)
            .putInt("user_id", userId)
            .putString("name", name)
            .putString("token", token)
            .putString("role", role ?: "user")
            .apply()
    }

    fun isLogin(): Boolean {
        return pref.getBoolean("is_login", false)
    }

    fun getUserId(): Int {
        return pref.getInt("user_id", 0)
    }

    fun getName(): String {
        return pref.getString("name", "") ?: ""
    }

    fun getRole(): String {
        return pref.getString("role", "user") ?: "user"
    }

    fun logout() {
        pref.edit().clear().apply()
    }
}