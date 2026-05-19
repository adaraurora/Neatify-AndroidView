package com.example.neatify.model

data class ServiceRequest(
    val nama_layanan: String,
    val deskripsi: String,
    val harga: Int,
    val satuan: String,
    val image: String
)