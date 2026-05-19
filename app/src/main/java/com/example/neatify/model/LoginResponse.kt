package com.example.neatify.model

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val token: String?,
    val data: User?
)