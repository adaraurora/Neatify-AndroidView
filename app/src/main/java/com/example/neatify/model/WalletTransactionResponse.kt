package com.example.neatify.model

data class WalletTransactionResponse(
    val status: Boolean,
    val message: String,
    val data: List<WalletTransaction>
)