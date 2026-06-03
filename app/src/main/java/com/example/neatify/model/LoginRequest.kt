package com.example.neatify.model

data class LoginRequest(
    val login: String,
    val password: String,
    val email: String = login,
    val phone: String = login
)
