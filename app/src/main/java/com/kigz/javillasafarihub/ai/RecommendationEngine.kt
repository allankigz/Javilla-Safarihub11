package com.kigz.javillasafarihub.ai

object RecommendationEngine {

    fun generateRecommendations(
        preferences: RecommendationPreferences
    ): List<SafariRecommendation> {

        val destinations = listOf(

            SafariRecommendation(
                destination = "Maasai Mara National Reserve",
                location = "Narok County",
                reason = "Excellent for wildlife viewing, game drives and the Great Migration.",
                activities = listOf(
                    "Game Drives",
                    "Wildlife Viewing",
                    "Bird Watching",
                    "Maasai Cultural Visits"
                ),
                estimatedCost = 45000.0,
                rating = 4.9,
                matchScore = 0
            ),

            SafariRecommendation(
                destination = "Amboseli National Park",
                location = "Kajiado County",
                reason = "Ideal for elephants, wildlife photography and views of Mount Kilimanjaro.",
                activities = listOf(
                    "Elephant Viewing",
                    "Game Drives",
                    "Photography",
                    "Bird Watching"
                ),
                estimatedCost = 38000.0,
                rating = 4.8,
                matchScore = 0
            ),

            SafariRecommendation(
                destination = "Tsavo East National Park",
                location = "Kenya Coast / Eastern Kenya",
                reason = "A great option for wildlife, large landscapes and adventurous safari experiences.",
                activities = listOf(
                    "Game Drives",
                    "Wildlife Viewing",
                    "Bird Watching",
                    "Photography"
                ),
                estimatedCost = 32000.0,
                rating = 4.7,
                matchScore = 0
            ),

            SafariRecommendation(
                destination = "Diani Beach",
                location = "Kwale County",
                reason = "A strong choice for travelers looking for beaches, relaxation and water activities.",
                activities = listOf(
                    "Swimming",
                    "Snorkeling",
                    "Beach Activities",
                    "Boat Rides"
                ),
                estimatedCost = 30000.0,
                rating = 4.8,
                matchScore = 0
            ),

            SafariRecommendation(
                destination = "Mount Kenya",
                location = "Central Kenya",
                reason = "Recommended for hiking, mountain experiences and outdoor adventure.",
                activities = listOf(
                    "Hiking",
                    "Mountain Climbing",
                    "Nature Walks",
                    "Photography"
                ),
                estimatedCost = 35000.0,
                rating = 4.7,
                matchScore = 0
            ),

            SafariRecommendation(
                destination = "Lake Nakuru National Park",
                location = "Nakuru County",
                reason = "A convenient option for wildlife, bird watching and scenic landscapes.",
                activities = listOf(
                    "Game Drives",
                    "Bird Watching",
                    "Wildlife Viewing",
                    "Photography"
                ),
                estimatedCost = 28000.0,
                rating = 4.6,
                matchScore = 0
            )
        )

        return destinations
            .map { destination ->

                var score = 50

                val interest = preferences.interest.lowercase()
                val style = preferences.travelStyle.lowercase()

                // ---------------------------------------------
                // INTEREST MATCHING
                // ---------------------------------------------

                when (interest) {

                    "wildlife" -> {
                        if (
                            destination.activities.any {
                                it.contains("wildlife", ignoreCase = true) ||
                                        it.contains("game", ignoreCase = true) ||
                                        it.contains("elephant", ignoreCase = true)
                            }
                        ) {
                            score += 25
                        }
                    }

                    "beach" -> {
                        if (
                            destination.destination.contains(
                                "Diani",
                                ignoreCase = true
                            )
                        ) {
                            score += 30
                        }

                        if (
                            destination.activities.any {
                                it.contains(
                                    "Beach",
                                    ignoreCase = true
                                ) ||
                                        it.contains(
                                            "Swimming",
                                            ignoreCase = true
                                        ) ||
                                        it.contains(
                                            "Snorkeling",
                                            ignoreCase = true
                                        )
                            }
                        ) {
                            score += 20
                        }
                    }

                    "adventure" -> {
                        if (
                            destination.activities.any {
                                it.contains(
                                    "Hiking",
                                    ignoreCase = true
                                ) ||
                                        it.contains(
                                            "Climbing",
                                            ignoreCase = true
                                        )
                            }
                        ) {
                            score += 30
                        }
                    }

                    "culture" -> {
                        if (
                            destination.activities.any {
                                it.contains(
                                    "Cultural",
                                    ignoreCase = true
                                )
                            }
                        ) {
                            score += 30
                        }
                    }

                    "photography" -> {
                        if (
                            destination.activities.any {
                                it.contains(
                                    "Photography",
                                    ignoreCase = true
                                )
                            }
                        ) {
                            score += 25
                        }
                    }
                }

                // ---------------------------------------------
                // BUDGET MATCHING
                // ---------------------------------------------

                if (preferences.budget > 0) {

                    if (
                        destination.estimatedCost <=
                        preferences.budget
                    ) {
                        score += 15
                    } else if (
                        destination.estimatedCost <=
                        preferences.budget * 1.2
                    ) {
                        score += 5
                    } else {
                        score -= 15
                    }
                }

                // ---------------------------------------------
                // TRAVEL STYLE
                // ---------------------------------------------

                when (style) {

                    "budget" -> {

                        if (
                            destination.estimatedCost <= 30000
                        ) {
                            score += 15
                        }
                    }

                    "luxury" -> {

                        if (
                            destination.rating >= 4.8
                        ) {
                            score += 10
                        }
                    }

                    "adventure" -> {

                        if (
                            destination.activities.any {
                                it.contains(
                                    "Hiking",
                                    ignoreCase = true
                                ) ||
                                        it.contains(
                                            "Climbing",
                                            ignoreCase = true
                                        )
                            }
                        ) {
                            score += 15
                        }
                    }

                    else -> {
                        score += 5
                    }
                }

                // ---------------------------------------------
                // GROUP SIZE
                // ---------------------------------------------

                if (preferences.travelers >= 4) {
                    score += 3
                }

                // ---------------------------------------------
                // TRIP DURATION
                // ---------------------------------------------

                if (preferences.days >= 4) {
                    score += 5
                }

                score = score.coerceIn(0, 100)

                destination.copy(
                    matchScore = score
                )
            }
            .sortedByDescending {
                it.matchScore
            }
            .take(5)
    }
}