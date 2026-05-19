package com.example.neatify.model

data class User(
    val id: Int,
    val name: String,
    val phone: String?,
    val email: String?,
    val alamat: String?,
    val role: String?,
    val saldo: Int?,
    val poin: Int?
)