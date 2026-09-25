package com.kigz.javillasafarihub.model

data class RecommendedTrip(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val duration: String,
    val estimatedPrice: Double,
    val activities: List<String>
)