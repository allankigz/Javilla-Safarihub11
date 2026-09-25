package com.kigz.javillasafarihub.model

data class Destination(

    // Unique ID
    val id: String,

    // Destination name
    val name: String,

    // County or location
    val location: String,

    // Description of the destination
    val description: String,

    // Image URL
    val imageUrl: String,

    // Tourism category
    val category: DestinationCategory,

    // Average rating
    val rating: Double,

    // Estimated entrance fee in Kenyan Shillings
    val entryFee: Double,

    // Best time to visit
    val bestTimeToVisit: String,

    // Available activities
    val activities: List<String>,

    // External website URL
    val websiteUrl: String = "",

    // Favorite status
    val isFavorite: Boolean = false
)

enum class DestinationCategory {

    NATIONAL_PARK,

    BEACH,

    MOUNTAIN,

    LAKE,

    CULTURAL,

    CITY,

    CONSERVANCY,

    HISTORICAL
}


