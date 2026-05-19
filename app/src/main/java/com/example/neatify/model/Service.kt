package com.example.neatify.model

data class Service(
    val id: Int,
    val nama_layanan: String,
    val deskripsi: String?,
    val harga: Int,
    val satuan: String,
    val image: String?
)