package com.kigz.javillasafarihub.reviews
enum class ReviewCategory(val label: String) {
    ALL("All"),
    DESTINATION("Destination"),
    EXPERIENCE("Experience"),
    SERVICE("Service"),
    RESTAURANT("Restaurant")
}

data class TouristReview(
    val id: String,
    val authorName: String,
    val title: String,
    val comment: String,
    val rating: Int,
    val category: ReviewCategory,
    val location: String,
    val serviceId: String = "",
    val verified: Boolean = false,
    val helpfulCount: Int = 0,
    val createdAtLabel: String = "Just now"
) {
    init {
        require(rating in 1..5) {
            "Rating must be between 1 and 5"
        }

        require(comment.isNotBlank()) {
            "Review comment cannot be empty"
        }
    }
}
