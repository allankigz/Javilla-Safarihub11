package com.kigz.javillasafarihub.safety

data class ScamReport(
    val id: String = "",
    val userId: String = "",
    val reporterName: String = "Traveller",
    val category: String = "Scam",
    val location: String = "",
    val description: String = "",
    val createdAt: Long = 0L,
    val moderationStatus: String = "pending",
    val confirmedCount: Int = 0
)
