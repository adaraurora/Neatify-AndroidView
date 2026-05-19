package com.example.neatify.model

data class Order(
    val id: Int,
    val kode_order: String?,
    val user_id: Int,
    val service_id: Int?,
    val layanan: String?,
    val berat: Double,
    val catatan: String?,
    val alamat: String?,
    val total_harga: Int,
    val ongkir: Int?,
    val diskon: Int?,
    val metode_pembayaran: String?,
    val status_pembayaran: String?,
    val status: String?,
    val estimasi_selesai: String?
)