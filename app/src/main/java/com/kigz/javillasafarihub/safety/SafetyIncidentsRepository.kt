package com.kigz.javillasafarihub.safety

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SafetyIncidentsRepository(
    private val database: FirebaseDatabase? = try { FirebaseDatabase.getInstance() } catch (_: Exception) { null },
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (_: Exception) { null }
) {
    private val incidentsRef = try { database?.getReference("safetyIncidents") } catch (_: Exception) { null }

    fun observeIncidents(onSuccess: (List<SafetyIncident>) -> Unit, onError: (String) -> Unit): ValueEventListener? {
        val ref = incidentsRef ?: run {
            onSuccess(emptyList())
            return null
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val incidents = snapshot.children.mapNotNull { child ->
                    val category = child.child("category").getValue(String::class.java)?.let { raw ->
                        IncidentCategory.entries.firstOrNull { it.name == raw }
                    } ?: return@mapNotNull null
                    SafetyIncident(
                        id = child.key ?: return@mapNotNull null,
                        category = category,
                        location = child.child("location").getValue(String::class.java).orEmpty(),
                        serviceId = child.child("serviceId").getValue(String::class.java).orEmpty(),
                        description = child.child("description").getValue(String::class.java).orEmpty(),
                        latitude = child.child("latitude").getValue(Double::class.java),
                        longitude = child.child("longitude").getValue(Double::class.java),
                        createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L,
                        moderationStatus = child.child("moderationStatus").getValue(String::class.java) ?: "pending",
                        confirmedCount = child.child("confirmedCount").getValue(Int::class.java) ?: 0
                    )
                }.filter { it.moderationStatus != "rejected" }
                    .sortedByDescending { it.createdAt }
                onSuccess(incidents)
            }
            override fun onCancelled(error: DatabaseError) { onError(error.message) }
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
            try { incidentsRef?.removeEventListener(listener) } catch (_: Exception) {}
        }
    }

    fun addIncident(
        category: IncidentCategory,
        location: String,
        description: String,
        serviceId: String,
        latitude: Double?,
        longitude: Double?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth?.currentUser
        if (user == null) { onError("Please sign in to report a safety incident."); return }
        val ref = incidentsRef ?: run { onError("Database is currently unavailable."); return }
        if (location.isBlank() || description.trim().length < 10) {
            onError("Add a location and at least 10 characters describing the incident."); return
        }
        val key = ref.push().key ?: run { onError("Could not create report ID."); return }
        val values = mutableMapOf<String, Any>(
            "id" to key,
            "userId" to user.uid,
            "reporterName" to (user.displayName ?: "Javilla traveller"),
            "category" to category.name,
            "location" to location.trim(),
            "description" to description.trim(),
            "serviceId" to serviceId.trim(),
            "createdAt" to ServerValue.TIMESTAMP,
            "moderationStatus" to "pending",
            "confirmedCount" to 0
        )
        if (latitude != null) values["latitude"] = latitude
        if (longitude != null) values["longitude"] = longitude
        ref.child(key).setValue(values)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Could not submit incident.") }
    }

    fun confirmIncident(id: String, onError: (String) -> Unit) {
        val uid = auth?.currentUser?.uid ?: run {
            onError("Please sign in to confirm an incident.")
            return
        }
        val db = database ?: run {
            onError("Database is currently unavailable.")
            return
        }
        try {
            val confirmationRef = db.reference.child("safetyIncidentConfirmations").child(id).child(uid)
            confirmationRef.setValue(true)
                .addOnFailureListener { onError(it.localizedMessage ?: "Could not record confirmation.") }
        } catch (_: Exception) {
            onError("Database is currently unavailable.")
        }
    }
}
