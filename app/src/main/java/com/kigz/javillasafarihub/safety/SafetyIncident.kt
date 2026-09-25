package com.kigz.javillasafarihub.safety

data class SafetyIncident(
    val id: String,
    val category: IncidentCategory,
    val location: String,
    val serviceId: String = "",
    val description: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Long = 0L,
    val moderationStatus: String = "pending",
    val confirmedCount: Int = 0
)
