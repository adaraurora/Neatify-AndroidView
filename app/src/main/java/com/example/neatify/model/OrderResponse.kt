package com.example.neatify.model

data class OrderResponse(
    val status: Boolean,
    val message: String,
    val data: Order?
)