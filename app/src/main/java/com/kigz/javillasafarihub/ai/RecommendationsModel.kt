package com.kigz.javillasafarihub.ai

data class RecommendationPreferences(
    val budget: Double = 0.0,
    val days: Int = 0,
    val travelers: Int = 1,
    val interest: String = "Wildlife",
    val travelStyle: String = "Balanced"
)

data class SafariRecommendation(
    val destination: String,
    val location: String,
    val reason: String,
    val activities: List<String>,
    val estimatedCost: Double,
    val rating: Double,
    val matchScore: Int
)