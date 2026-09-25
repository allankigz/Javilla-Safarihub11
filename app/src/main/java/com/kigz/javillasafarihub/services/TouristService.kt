package com.kigz.javillasafarihub.services

data class TouristService(
    val id: String = "",
    val name: String = "",
    val category: String = "Experience",
    val location: String = "Kenya",
    val priceGuide: String = "Confirm price before booking",
    val description: String = "",
    val phone: String = "",
    val website: String = "",
    val verified: Boolean = false,
    val verificationStatus: String = "pending",
    val verificationNote: String = "Verification details are being reviewed.",
    val active: Boolean = true,
    val updatedAt: Long = 0L
)
