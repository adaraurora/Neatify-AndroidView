package com.example.neatify.model

data class OrderListResponse(
    val status: Boolean,
    val message: String,
    val data: List<Order>
)