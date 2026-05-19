package com.example.neatify.model

data class RegisterRequest(
    val name: String,
    val phone: String,
    val email: String?,
    val password: String
)