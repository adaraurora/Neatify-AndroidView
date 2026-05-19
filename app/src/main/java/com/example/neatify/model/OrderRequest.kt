package com.example.neatify.model

data class OrderRequest(
    val user_id: Int,
    val service_id: Int,
    val berat: Double,
    val alamat: String,
    val catatan: String?,
    val metode_pembayaran: String?
)