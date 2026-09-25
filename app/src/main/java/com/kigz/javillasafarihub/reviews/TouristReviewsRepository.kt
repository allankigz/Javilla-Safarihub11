package com.kigz.javillasafarihub.reviews

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class TouristReviewsRepository(
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (_: Exception) { null },
    private val database: FirebaseDatabase? = try { FirebaseDatabase.getInstance() } catch (_: Exception) { null }
) {
    private val reviewsRef = try { database?.reference?.child("reviews") } catch (_: Exception) { null }

    fun observeReviews(
        onSuccess: (List<TouristReview>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener? {
        val ref = reviewsRef ?: run {
            onSuccess(emptyList())
            return null
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = snapshot.children.mapNotNull { child ->
                    val rating = child.child("rating").getValue(Int::class.java) ?: return@mapNotNull null
                    val categoryName = child.child("category").getValue(String::class.java) ?: "SERVICE"
                    val category = ReviewCategory.entries.firstOrNull { it.name == categoryName } ?: ReviewCategory.SERVICE
                    TouristReview(
                        id = child.key.orEmpty(),
                        authorName = child.child("authorName").getValue(String::class.java).orEmpty().ifBlank { "Traveller" },
                        title = child.child("title").getValue(String::class.java).orEmpty().ifBlank { "Traveller Review" },
                        comment = child.child("comment").getValue(String::class.java).orEmpty(),
                        rating = rating.coerceIn(1, 5),
                        category = category,
                        location = child.child("location").getValue(String::class.java).orEmpty(),
                        serviceId = child.child("serviceId").getValue(String::class.java).orEmpty(),
                        verified = child.child("verified").getValue(Boolean::class.java) ?: false,
                        helpfulCount = child.child("helpfulCount").getValue(Int::class.java) ?: 0,
                        createdAtLabel = child.child("createdAtLabel").getValue(String::class.java) ?: "Recently"
                    )
                }.filter { it.comment.isNotBlank() && it.location.isNotBlank() }
                    .sortedByDescending { it.id }
                onSuccess(reviews)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }
        try {
            ref.addValueEventListener(listener)
        } catch (e: Exception) {
            onSuccess(emptyList())
            return null
        }
        return listener
    }

    fun removeListener(listener: ValueEventListener?) {
        if (listener != null) {
            try { reviewsRef?.removeEventListener(listener) } catch (_: Exception) {}
        }
    }

    fun addReview(
        review: TouristReview,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth?.currentUser ?: run {
            onError("Please sign in before submitting a review.")
            return
        }
        val ref = reviewsRef ?: run {
            onError("Database is currently unavailable.")
            return
        }
        val key = ref.push().key ?: run {
            onError("Could not create a review ID.")
            return
        }
        val values = mapOf<String, Any?>(
            "id" to key,
            "userId" to user.uid,
            "authorName" to (user.displayName ?: "Traveller"),
            "title" to review.title,
            "comment" to review.comment,
            "rating" to review.rating,
            "category" to review.category.name,
            "location" to review.location,
            "serviceId" to review.serviceId,
            "verified" to false,
            "helpfulCount" to 0,
            "createdAt" to ServerValue.TIMESTAMP,
            "createdAtLabel" to "Recently",
            "moderationStatus" to "pending"
        )
        ref.child(key).setValue(values)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Could not submit review.") }
    }

    fun incrementHelpful(reviewId: String, onError: (String) -> Unit) {
        val uid = auth?.currentUser?.uid ?: run {
            onError("Please sign in to mark a review helpful.")
            return
        }
        val db = database ?: run {
            onError("Database is currently unavailable.")
            return
        }
        try {
            db.reference.child("reviewHelpful").child(reviewId).child(uid).setValue(true)
                .addOnFailureListener { onError(it.localizedMessage ?: "Could not record helpful vote.") }
        } catch (_: Exception) {
            onError("Database is currently unavailable.")
        }
    }
}
