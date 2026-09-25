package com.kigz.javillasafarihub.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class VerifiedServicesRepository(
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (_: Exception) { null },
    private val database: FirebaseDatabase? = try { FirebaseDatabase.getInstance() } catch (_: Exception) { null }
) {
    private val servicesRef = try { database?.reference?.child("services") } catch (_: Exception) { null }
    private val reportsRef = try { database?.reference?.child("serviceReports") } catch (_: Exception) { null }

    fun observeServices(onSuccess: (List<TouristService>) -> Unit, onError: (String) -> Unit): ValueEventListener? {
        val ref = servicesRef ?: run {
            onSuccess(sampleServices)
            return null
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val services = snapshot.children.mapNotNull { child ->
                    if (child.child("active").getValue(Boolean::class.java) == false) return@mapNotNull null
                    TouristService(
                        id = child.key.orEmpty(),
                        name = child.child("name").getValue(String::class.java).orEmpty(),
                        category = child.child("category").getValue(String::class.java) ?: "Experience",
                        location = child.child("location").getValue(String::class.java) ?: "Kenya",
                        priceGuide = child.child("priceGuide").getValue(String::class.java) ?: "Confirm price before booking",
                        description = child.child("description").getValue(String::class.java).orEmpty(),
                        phone = child.child("phone").getValue(String::class.java).orEmpty(),
                        website = child.child("website").getValue(String::class.java).orEmpty(),
                        verified = child.child("verified").getValue(Boolean::class.java) ?: false,
                        verificationStatus = child.child("verificationStatus").getValue(String::class.java) ?: "pending",
                        verificationNote = child.child("verificationNote").getValue(String::class.java) ?: "Verification details are being reviewed.",
                        active = true,
                        updatedAt = child.child("updatedAt").getValue(Long::class.java) ?: 0L
                    ).takeIf { it.name.isNotBlank() }
                }.sortedBy { it.name.lowercase() }
                onSuccess(if (services.isEmpty()) sampleServices else services)
            }
            override fun onCancelled(error: DatabaseError) { onError(error.message) }
        }
        try {
            ref.addValueEventListener(listener)
        } catch (_: Exception) {
            onSuccess(sampleServices)
            return null
        }
        return listener
    }

    fun removeListener(listener: ValueEventListener?) {
        if (listener != null) {
            try { servicesRef?.removeEventListener(listener) } catch (_: Exception) {}
        }
    }

    fun reportService(service: TouristService, reason: String, details: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth?.currentUser ?: run {
            onError("Please sign in before reporting a service.")
            return
        }
        val ref = reportsRef ?: run {
            onError("Database is currently unavailable.")
            return
        }
        val key = ref.push().key ?: run {
            onError("Could not create the service report.")
            return
        }
        val values = mapOf<String, Any?>(
            "id" to key,
            "serviceId" to service.id,
            "serviceName" to service.name,
            "userId" to user.uid,
            "reporterName" to (user.displayName ?: "Traveller"),
            "reason" to reason,
            "details" to details,
            "status" to "pending",
            "createdAt" to ServerValue.TIMESTAMP
        )
        ref.child(key).setValue(values)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Could not submit service report.") }
    }
}

private val sampleServices = listOf(
    TouristService("sample-savanna-trails", "Savanna Trails Tours", "Tour Operator", "Nairobi", "Quote before booking", "Safari planning, transfers and guided wildlife experiences.", verified = true, verificationStatus = "verified", verificationNote = "Prototype verified listing; production verification should use official records and partner checks."),
    TouristService("sample-coastal-escape", "Coastal Escape Guides", "Tour Guide", "Mombasa", "Confirm guide fee", "Local coastal tours, culture and attraction guidance.", verified = true, verificationStatus = "verified", verificationNote = "Prototype verified listing; production verification should use official records and partner checks."),
    TouristService("sample-safari-haven", "Safari Haven Lodge", "Accommodation", "Maasai Mara", "Check current room rate", "Safari accommodation with access to local wildlife experiences.", verified = true, verificationStatus = "verified", verificationNote = "Prototype verified listing; production verification should use official records and partner checks."),
    TouristService("sample-nairobi-eats", "Nairobi City Eats", "Restaurant", "Nairobi", "Menu prices shown on request", "Casual dining option for travellers exploring the city.", verified = true, verificationStatus = "verified", verificationNote = "Prototype verified listing; production verification should use official records and partner checks."),
    TouristService("sample-reliable-ride", "Reliable Ride Kenya", "Transport", "Nairobi & major routes", "Agree fare before travel", "Tourist-oriented transfers and airport-to-hotel transport.", verified = true, verificationStatus = "verified", verificationNote = "Prototype verified listing; production verification should use official records and partner checks."),
    TouristService("sample-lakeview-cultural", "Lakeview Cultural Experiences", "Experience", "Kisumu", "Ask for full package price", "Community-oriented cultural experiences and local storytelling.", verified = true, verificationStatus = "verified", verificationNote = "Prototype verified listing; production verification should use official records and partner checks.")
)
