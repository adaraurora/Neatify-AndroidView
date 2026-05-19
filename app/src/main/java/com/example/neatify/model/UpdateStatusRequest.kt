package com.example.neatify.model

data class UpdateStatusRequest(
    val admin_id: Int,
    val status: String
)