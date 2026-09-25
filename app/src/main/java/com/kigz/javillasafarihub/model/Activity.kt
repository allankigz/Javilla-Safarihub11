package com.kigz.javillasafarihub.model


data class Activity(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val category: ActivityCategory,
    val rating: Double,
    val price: Double,
    val bestTimeToVisit: String,
    val imageUrl: String = "",
    val isFavorite: Boolean = false
)
