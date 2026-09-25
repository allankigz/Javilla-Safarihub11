package com.kigz.javillasafarihub.safety

data class TouristIncident(
    val id: String,
    val category: IncidentCategory,
    val location: String,
    val description: String,
    val submittedAt: String = "Just now"
)