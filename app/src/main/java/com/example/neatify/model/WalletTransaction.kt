package com.example.neatify.model

data class WalletTransaction(
    val id: Int,
    val user_id: Int,
    val type: String,
    val amount: Int,
    val description: String,
    val created_at: String?
)