package com.kigz.javillasafarihub.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kigz.javillasafarihub.model.Activity
import com.kigz.javillasafarihub.model.ActivityCategory

object ActivityRepository {

    private val activities = listOf(

        Activity(
            id = "wildlife_safari",
            name = "Wildlife Safari",
            description = "Experience Kenya's amazing wildlife in its natural habitat.",
            location = "Maasai Mara",
            category = ActivityCategory.WILDLIFE_SAFARI,
            rating = 4.9,
            price = 5000.0,
            bestTimeToVisit = "July - October"
        ),

        Activity(
            id = "hiking",
            name = "Hiking",
            description = "Explore beautiful trails, forests and scenic landscapes.",
            location = "Mount Kenya",
            category = ActivityCategory.HIKING,
            rating = 4.7,
            price = 2500.0,
            bestTimeToVisit = "January - February"
        ),

        Activity(
            id = "beach_activities",
            name = "Beach Activities",
            description = "Enjoy relaxing beaches and exciting coastal activities.",
            location = "Diani Beach",
            category = ActivityCategory.BEACH,
            rating = 4.8,
            price = 3000.0,
            bestTimeToVisit = "June - October"
        ),

        Activity(
            id = "elephant_viewing",
            name = "Elephant Viewing",
            description = "Get an unforgettable opportunity to see elephants in their natural environment.",
            location = "Amboseli",
            category = ActivityCategory.ELEPHANT_VIEWING,
            rating = 4.8,
            price = 4500.0,
            bestTimeToVisit = "June - October"
        ),

        Activity(
            id = "mountain_climbing",
            name = "Mountain Climbing",
            description = "Challenge yourself with an unforgettable mountain climbing experience.",
            location = "Mount Kenya",
            category = ActivityCategory.MOUNTAIN_CLIMBING,
            rating = 4.7,
            price = 8000.0,
            bestTimeToVisit = "January - February"
        ),

        Activity(
            id = "diving_snorkeling",
            name = "Diving & Snorkeling",
            description = "Discover the beautiful marine life and coral reefs of Kenya's coast.",
            location = "Watamu",
            category = ActivityCategory.DIVING_SNORKELING,
            rating = 4.6,
            price = 6000.0,
            bestTimeToVisit = "October - March"
        ),

        Activity(
            id = "cycling",
            name = "Cycling",
            description = "Explore Kenya's landscapes and countryside by bicycle.",
            location = "Hell's Gate",
            category = ActivityCategory.CYCLING,
            rating = 4.5,
            price = 2000.0,
            bestTimeToVisit = "June - October"
        ),

        Activity(
            id = "boat_ride",
            name = "Boat Ride",
            description = "Enjoy a peaceful and exciting boat ride surrounded by beautiful scenery.",
            location = "Lake Naivasha",
            category = ActivityCategory.BOAT_RIDE,
            rating = 4.5,
            price = 2500.0,
            bestTimeToVisit = "June - October"
        ),

        Activity(
            id = "bird_watching",
            name = "Bird Watching",
            description = "Discover Kenya's wide variety of beautiful bird species.",
            location = "Lake Nakuru",
            category = ActivityCategory.BIRD_WATCHING,
            rating = 4.6,
            price = 1500.0,
            bestTimeToVisit = "November - April"
        ),

        Activity(
            id = "camping",
            name = "Camping",
            description = "Spend time outdoors and experience Kenya's natural beauty through camping.",
            location = "Tsavo",
            category = ActivityCategory.CAMPING,
            rating = 4.5,
            price = 3500.0,
            bestTimeToVisit = "June - October"
        ),

        Activity(
            id = "cultural_tour",
            name = "Cultural Tour",
            description = "Learn about Kenyan communities, traditions, food and culture.",
            location = "Maasai Village",
            category = ActivityCategory.CULTURAL_TOUR,
            rating = 4.7,
            price = 3000.0,
            bestTimeToVisit = "All Year"
        ),

        Activity(
            id = "photography",
            name = "Photography",
            description = "Capture Kenya's wildlife, landscapes, culture and unforgettable moments.",
            location = "Maasai Mara",
            category = ActivityCategory.PHOTOGRAPHY,
            rating = 4.9,
            price = 2000.0,
            bestTimeToVisit = "July - October"
        )
    )

    private val remoteActivities = mutableListOf<Activity>()
    private val remoteDisabledActivityIds = mutableSetOf<String>()
    private var remoteListener: ValueEventListener? = null
    private var remoteListenerStarted = false
    private var contentChangeCallback: (() -> Unit)? = null

    private fun ensureRemoteListener() {
        if (remoteListenerStarted || FirebaseAuth.getInstance().currentUser == null) return
        remoteListenerStarted = true
        runCatching {
            remoteListener = FirebaseDatabase.getInstance().reference.child("content").child("activities")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        remoteActivities.clear()
                        remoteDisabledActivityIds.clear()
                        snapshot.children.forEach { child ->
                            val childId = child.key.orEmpty()
                            if (child.child("active").getValue(Boolean::class.java) == false) {
                                if (childId.isNotBlank()) remoteDisabledActivityIds += childId
                                return@forEach
                            }
                            val id = child.key.orEmpty()
                            val name = child.child("name").getValue(String::class.java).orEmpty()
                            val location = child.child("location").getValue(String::class.java).orEmpty()
                            val category = child.child("category").getValue(String::class.java)?.let { raw -> ActivityCategory.entries.firstOrNull { it.name == raw } }
                            if (id.isBlank() || name.isBlank() || location.isBlank() || category == null) return@forEach
                            remoteActivities += Activity(
                                id = id,
                                name = name,
                                description = child.child("description").getValue(String::class.java).orEmpty(),
                                location = location,
                                category = category,
                                rating = child.child("rating").getValue(Double::class.java) ?: 0.0,
                                price = child.child("price").getValue(Double::class.java) ?: 0.0,
                                bestTimeToVisit = child.child("bestTimeToVisit").getValue(String::class.java).orEmpty(),
                                imageUrl = child.child("imageUrl").getValue(String::class.java).orEmpty()
                            )
                        }
                        contentChangeCallback?.invoke()
                    }
                    override fun onCancelled(error: DatabaseError) { /* local fallback remains available */ }
                })
        }
    }

    fun getActivities(): List<Activity> {
        ensureRemoteListener()
        val remoteById = remoteActivities.associateBy { it.id }
        return activities.filter { it.id !in remoteDisabledActivityIds }.map { remoteById[it.id] ?: it } + remoteActivities.filter { remote -> activities.none { it.id == remote.id } }
    }

    fun observeContentChanges(onChanged: () -> Unit) {
        contentChangeCallback = onChanged
        onChanged()
        ensureRemoteListener()
    }

    fun clearContentObserver() {
        contentChangeCallback = null
    }

    fun close() {
        remoteListener?.let { listener ->
            runCatching { FirebaseDatabase.getInstance().reference.child("content").child("activities").removeEventListener(listener) }
        }
        remoteListener = null
        remoteListenerStarted = false
    }

    fun getActivityById(id: String): Activity? {
        return getActivities().find { it.id == id }
    }

    fun getActivitiesByCategory(
        category: ActivityCategory
    ): List<Activity> {
        return getActivities().filter {
            it.category == category
        }
    }

    fun searchActivities(
        query: String
    ): List<Activity> {

        val source = getActivities()
        if (query.isBlank()) {
            return source
        }

        return source.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.location.contains(query, ignoreCase = true) ||
                    it.category.name.contains(query, ignoreCase = true)
        }
    }
}