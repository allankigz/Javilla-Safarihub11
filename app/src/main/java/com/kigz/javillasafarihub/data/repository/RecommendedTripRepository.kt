package com.kigz.javillasafarihub.data.repository

import com.kigz.javillasafarihub.model.RecommendedTrip

object RecommendedTripRepository {

    private val recommendedTrips = listOf(

        RecommendedTrip(
            id = "maasai_mara_safari",
            name = "Maasai Mara Safari Adventure",
            description = "Experience Kenya's famous wildlife and enjoy an unforgettable safari adventure.",
            location = "Maasai Mara",
            duration = "3 Days",
            estimatedPrice = 15000.0,
            activities = listOf(
                "Wildlife Safari",
                "Photography",
                "Cultural Tour"
            )
        ),

        RecommendedTrip(
            id = "mount_kenya_adventure",
            name = "Mount Kenya Adventure",
            description = "Explore mountain landscapes and enjoy an exciting hiking and climbing experience.",
            location = "Mount Kenya",
            duration = "4 Days",
            estimatedPrice = 20000.0,
            activities = listOf(
                "Hiking",
                "Mountain Climbing",
                "Photography"
            )
        ),

        RecommendedTrip(
            id = "coastal_escape",
            name = "Kenya Coastal Escape",
            description = "Relax along the coast while enjoying exciting marine and beach activities.",
            location = "Diani Beach & Watamu",
            duration = "4 Days",
            estimatedPrice = 18000.0,
            activities = listOf(
                "Beach Activities",
                "Diving & Snorkeling",
                "Boat Ride"
            )
        ),

        RecommendedTrip(
            id = "lake_experience",
            name = "Kenya Lakes Experience",
            description = "Discover beautiful lakes while enjoying bird watching, boat rides and nature.",
            location = "Lake Naivasha & Lake Nakuru",
            duration = "3 Days",
            estimatedPrice = 12000.0,
            activities = listOf(
                "Boat Ride",
                "Bird Watching",
                "Photography"
            )
        ),

        RecommendedTrip(
            id = "tsavo_wildlife",
            name = "Tsavo Wildlife Adventure",
            description = "Explore Kenya's wilderness and experience wildlife and outdoor camping.",
            location = "Tsavo",
            duration = "3 Days",
            estimatedPrice = 14000.0,
            activities = listOf(
                "Wildlife Safari",
                "Camping",
                "Photography"
            )
        )
    )

    fun getRecommendedTrips(): List<RecommendedTrip> {
        return recommendedTrips
    }

    fun getRecommendedTripById(
        id: String
    ): RecommendedTrip? {
        return recommendedTrips.find {
            it.id == id
        }
    }
}