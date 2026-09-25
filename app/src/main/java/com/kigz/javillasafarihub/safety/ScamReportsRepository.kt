package com.kigz.javillasafarihub.safety

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class ScamReportsRepository(
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (_: Exception) { null },
    private val database: FirebaseDatabase? = try { FirebaseDatabase.getInstance() } catch (_: Exception) { null }
) {
    private val reportsRef = try { database?.reference?.child("scamReports") } catch (_: Exception) { null }

    fun observeReports(
        onSuccess: (List<ScamReport>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener? {
        val ref = reportsRef ?: run {
            onSuccess(emptyList())
            return null
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reports = snapshot.children.mapNotNull { child ->
                    val description = child.child("description").getValue(String::class.java).orEmpty()
                    val location = child.child("location").getValue(String::class.java).orEmpty()
                    if (description.isBlank() || location.isBlank()) return@mapNotNull null
                    ScamReport(
                        id = child.key.orEmpty(),
                        userId = child.child("userId").getValue(String::class.java).orEmpty(),
                        reporterName = child.child("reporterName").getValue(String::class.java).orEmpty().ifBlank { "Traveller" },
                        category = child.child("category").getValue(String::class.java) ?: "Scam",
                        location = location,
                        description = description,
                        createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L,
                        moderationStatus = child.child("moderationStatus").getValue(String::class.java) ?: "pending",
                        confirmedCount = child.child("confirmedCount").getValue(Int::class.java) ?: 0
                    )
                }.filter { it.moderationStatus != "rejected" }
                    .sortedByDescending { it.createdAt }
                onSuccess(reports)
            }

            override fun onCancelled(error: DatabaseError) = onError(error.message)
        }
        try {
            ref.addValueEventListener(listener)
        } catch (_: Exception) {
            onSuccess(emptyList())
            return null
        }
        return listener
    }

    fun removeListener(listener: ValueEventListener?) {
        if (listener != null) {
            try { reportsRef?.removeEventListener(listener) } catch (_: Exception) {}
        }
    }

    fun addReport(category: String, location: String, description: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth?.currentUser ?: run {
            onError("Please sign in before submitting a report.")
            return
        }
        val ref = reportsRef ?: run {
            onError("Database is currently unavailable.")
            return
        }
        val key = ref.push().key ?: run {
            onError("Could not create a report ID.")
            return
        }
        val values = mapOf<String, Any?>(
            "id" to key,
            "userId" to user.uid,
            "reporterName" to (user.displayName ?: "Traveller"),
            "category" to category,
            "location" to location.trim(),
            "description" to description.trim(),
            "createdAt" to ServerValue.TIMESTAMP,
            "moderationStatus" to "pending",
            "confirmedCount" to 0
        )
        ref.child(key).setValue(values)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Could not submit report.") }
    }

    fun confirmReport(reportId: String, onError: (String) -> Unit) {
        val uid = auth?.currentUser?.uid ?: run {
            onError("Please sign in to confirm a report.")
            return
        }
        val db = database ?: run {
            onError("Database is currently unavailable.")
            return
        }
        try {
            val confirmationRef = db.reference.child("scamReportConfirmations").child(reportId).child(uid)
            confirmationRef.setValue(true)
                .addOnFailureListener { onError(it.localizedMessage ?: "Could not record confirmation.") }
        } catch (_: Exception) {
            onError("Database is currently unavailable.")
        }
    }
}
