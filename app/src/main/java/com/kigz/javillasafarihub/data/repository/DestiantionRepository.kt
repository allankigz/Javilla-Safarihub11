package com.kigz.javillasafarihub.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.kigz.javillasafarihub.model.Destination
import com.kigz.javillasafarihub.model.DestinationCategory

class DestinationRepository {

    private val auth by lazy { 
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }
    private val favoritesRef by lazy { 
        auth?.currentUser?.uid?.let { uid ->
            try { FirebaseDatabase.getInstance().reference.child("users").child(uid).child("favorites") } catch (e: Exception) { null }
        }
    }

    private val remoteDestinations = mutableListOf<Destination>()
    private val remoteDisabledDestinationIds = mutableSetOf<String>()
    private var remoteContentListener: ValueEventListener? = null
    private var contentChangeCallback: (() -> Unit)? = null

    init {
        val uid = auth?.currentUser?.uid
        if (uid != null) {
            runCatching {
                remoteContentListener = FirebaseDatabase.getInstance().reference.child("content").child("destinations")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                            remoteDestinations.clear()
                            remoteDisabledDestinationIds.clear()
                            snapshot.children.forEach { child ->
                                val childId = child.key.orEmpty()
                                if (child.child("active").getValue(Boolean::class.java) == false) {
                                    if (childId.isNotBlank()) remoteDisabledDestinationIds += childId
                                    return@forEach
                                }
                                val id = child.key.orEmpty()
                                val category = child.child("category").getValue(String::class.java)?.let { raw ->
                                    DestinationCategory.entries.firstOrNull { it.name == raw }
                                } ?: return@forEach
                                val name = child.child("name").getValue(String::class.java).orEmpty()
                                val location = child.child("location").getValue(String::class.java).orEmpty()
                                if (id.isBlank() || name.isBlank() || location.isBlank()) return@forEach
                                remoteDestinations += Destination(
                                    id = id,
                                    name = name,
                                    location = location,
                                    description = child.child("description").getValue(String::class.java).orEmpty(),
                                    imageUrl = child.child("imageUrl").getValue(String::class.java).orEmpty(),
                                    category = category,
                                    rating = child.child("rating").getValue(Double::class.java) ?: 0.0,
                                    entryFee = child.child("entryFee").getValue(Double::class.java) ?: 0.0,
                                    bestTimeToVisit = child.child("bestTimeToVisit").getValue(String::class.java).orEmpty(),
                                    activities = child.child("activities").children.mapNotNull { it.getValue(String::class.java) },
                                    websiteUrl = child.child("websiteUrl").getValue(String::class.java).orEmpty()
                                )
                            }
                            contentChangeCallback?.invoke()
                        }
                        override fun onCancelled(error: com.google.firebase.database.DatabaseError) { /* local fallback remains available */ }
                    })
            }
        }
    }

    fun observeContentChanges(onChanged: () -> Unit) {
        contentChangeCallback = onChanged
        onChanged()
    }

    fun clearContentObserver() {
        contentChangeCallback = null
    }

    fun close() {
        remoteContentListener?.let { listener ->
            runCatching { FirebaseDatabase.getInstance().reference.child("content").child("destinations").removeEventListener(listener) }
        }
        remoteContentListener = null
    }

    // ---------------------------------------------------------
    // KENYAN TOURISM DESTINATIONS
    // ---------------------------------------------------------

    private val destinations = listOf(

        // 1. Maasai Mara
        Destination(
            id = "maasai_mara",
            name = "Maasai Mara National Reserve",
            location = "Narok County",
            description =
                "One of Kenya's most famous wildlife destinations, " +
                        "known for the Big Five and the Great Migration.",
            imageUrl = "https://images.pexels.com/photos/30705114/pexels-photo-30705114.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.NATIONAL_PARK,
            rating = 4.9,
            entryFee = 0.0,
            bestTimeToVisit = "July - October",
            activities = listOf(
                "Game Drives",
                "Great Migration",
                "Bird Watching",
                "Maasai Cultural Visits"
            ),
            websiteUrl = "https://maasai-mara-reserve.com/"
        ),

        // 2. Amboseli
        Destination(
            id = "amboseli",
            name = "Amboseli National Park",
            location = "Kajiado County",
            description =
                "A spectacular national park famous for large " +
                        "elephant herds and views of Mount Kilimanjaro.",
            imageUrl = "https://images.pexels.com/photos/26924191/pexels-photo-26924191.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.NATIONAL_PARK,
            rating = 4.8,
            entryFee = 0.0,
            bestTimeToVisit = "June - October",
            activities = listOf(
                "Game Drives",
                "Elephant Viewing",
                "Bird Watching",
                "Photography"
            ),
            websiteUrl = "https://kws.go.ke/park/amboseli-national-park/"
        ),

        // 3. Tsavo
        Destination(
            id = "tsavo",
            name = "Tsavo East National Park",
            location = "Taita-Taveta County",
            description =
                "One of Kenya's largest protected areas, " +
                        "offering diverse wildlife and dramatic landscapes.",
            imageUrl = "https://images.pexels.com/photos/13242022/pexels-photo-13242022.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.NATIONAL_PARK,
            rating = 4.7,
            entryFee = 0.0,
            bestTimeToVisit = "June - October",
            activities = listOf(
                "Game Drives",
                "Wildlife Viewing",
                "Bird Watching",
                "Photography"
            ),
            websiteUrl = "https://kws.go.ke/park/tsavo-east-national-park/"
        ),

        // 4. Lake Nakuru
        Destination(
            id = "lake_nakuru",
            name = "Lake Nakuru National Park",
            location = "Nakuru County",
            description =
                "A scenic wildlife destination known for rhinos, " +
                        "lions, flamingos and beautiful landscapes.",
            imageUrl = "https://images.pexels.com/photos/20255307/pexels-photo-20255307.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.LAKE,
            rating = 4.6,
            entryFee = 0.0,
            bestTimeToVisit = "June - March",
            activities = listOf(
                "Game Drives",
                "Bird Watching",
                "Rhino Viewing",
                "Photography"
            ),
            websiteUrl = "https://kws.go.ke/park/lake-nakuru-national-park/"
        ),

        // 5. Diani Beach
        Destination(
            id = "diani",
            name = "Diani Beach",
            location = "Kwale County",
            description =
                "A beautiful coastal destination famous for its " +
                        "white sandy beaches and warm Indian Ocean waters.",
            imageUrl = "https://images.pexels.com/photos/12858509/pexels-photo-12858509.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.BEACH,
            rating = 4.8,
            entryFee = 0.0,
            bestTimeToVisit = "June - October",
            activities = listOf(
                "Swimming",
                "Snorkeling",
                "Diving",
                "Beach Walks",
                "Water Sports"
            ),
            websiteUrl = "https://tourkenya.go.ke/tourism/marine-beach-tourism/"
        ),

        // 6. Mount Kenya
        Destination(
            id = "mount_kenya",
            name = "Mount Kenya",
            location = "Central Kenya",
            description =
                "Africa's second-highest mountain offering " +
                        "spectacular scenery and challenging hiking routes.",
            imageUrl = "https://images.pexels.com/photos/12021270/pexels-photo-12021270.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.MOUNTAIN,
            rating = 4.8,
            entryFee = 0.0,
            bestTimeToVisit = "January - February",
            activities = listOf(
                "Mountain Climbing",
                "Hiking",
                "Camping",
                "Wildlife Viewing"
            ),
            websiteUrl = "https://kws.go.ke/park/mt-kenya-national-park/"
        ),

        // 7. Lamu
        Destination(
            id = "lamu",
            name = "Lamu Old Town",
            location = "Lamu County",
            description =
                "A historic coastal town famous for its Swahili " +
                        "architecture, culture and rich history.",
            imageUrl = "https://images.pexels.com/photos/5702144/pexels-photo-5702144.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.HISTORICAL,
            rating = 4.7,
            entryFee = 0.0,
            bestTimeToVisit = "June - October",
            activities = listOf(
                "Cultural Tours",
                "Historical Tours",
                "Boat Trips",
                "Beach Activities"
            ),
            websiteUrl = "https://museums.or.ke/lamu-old-town/"
        ),

        // 8. Nairobi
        Destination(
            id = "nairobi",
            name = "Nairobi",
            location = "Nairobi County",
            description =
                "Kenya's capital city offering wildlife, culture, " +
                        "shopping, restaurants and modern attractions.",
            imageUrl = "https://images.pexels.com/photos/29069344/pexels-photo-29069344.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.CITY,
            rating = 4.5,
            entryFee = 0.0,
            bestTimeToVisit = "June - October",
            activities = listOf(
                "City Tours",
                "Nairobi National Park",
                "Shopping",
                "Cultural Experiences"
            ),
            websiteUrl = "https://www.visitnairobikenya.com/destinations/nairobi"
        ),

        // 9. Hell's Gate
        Destination(
            id = "hells_gate",
            name = "Hell's Gate National Park",
            location = "Nakuru County",
            description =
                "A unique park where visitors can explore dramatic " +
                        "cliffs, gorges and wildlife on foot or by bicycle.",
            imageUrl = "https://images.pexels.com/photos/26285568/pexels-photo-26285568.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.NATIONAL_PARK,
            rating = 4.6,
            entryFee = 0.0,
            bestTimeToVisit = "June - October",
            activities = listOf(
                "Cycling",
                "Hiking",
                "Rock Climbing",
                "Wildlife Viewing"
            ),
            websiteUrl = "https://kws.go.ke/park/hells-gate-national-park/"
        ),

        // 10. Lake Naivasha
        Destination(
            id = "lake_naivasha",
            name = "Lake Naivasha",
            location = "Nakuru County",
            description =
                "A freshwater lake in the Great Rift Valley known " +
                        "for boat rides, hippos and bird watching.",
            imageUrl = "https://images.pexels.com/photos/16984816/pexels-photo-16984816.jpeg?auto=compress&cs=tinysrgb&w=1200",
            category = DestinationCategory.LAKE,
            rating = 4.5,
            entryFee = 0.0,
            bestTimeToVisit = "June - October",
            activities = listOf(
                "Boat Rides",
                "Bird Watching",
                "Wildlife Viewing",
                "Nature Walks"
            ),
            websiteUrl = "https://nakuru.go.ke/naivasha-municipality/"
        )
    )

    // ---------------------------------------------------------
    // FAVORITES
    // ---------------------------------------------------------

    /*
     * Stores the IDs of destinations marked as favorites.
     *
     * Firebase is the persistent source of truth; this set mirrors the
     * currently authenticated user's favorites for fast Compose reads.
     */
    private val favoriteIds = mutableSetOf<String>()

    init {
        favoritesRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                favoriteIds.clear()
                snapshot.children.forEach { child ->
                    if (child.getValue(Boolean::class.java) == true) favoriteIds.add(child.key.orEmpty())
                }
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) = Unit
        })
    }


    // ---------------------------------------------------------
    // REAL-TIME FAVORITES
    // ---------------------------------------------------------

    fun observeFavorites(onChanged: (List<Destination>) -> Unit): ValueEventListener? {
        val ref = favoritesRef
        if (ref == null) {
            favoriteIds.clear()
            onChanged(emptyList())
            return null
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                favoriteIds.clear()
                snapshot.children.forEach { child ->
                    if (child.getValue(Boolean::class.java) == true) favoriteIds.add(child.key.orEmpty())
                }
                onChanged(destinations.filter { it.id in favoriteIds }.map { it.copy(isFavorite = true) })
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                onChanged(emptyList())
            }
        }
        ref.addValueEventListener(listener)
        return listener
    }

    fun removeFavoritesListener(listener: ValueEventListener?) {
        if (listener != null) favoritesRef?.removeEventListener(listener)
    }

    // ---------------------------------------------------------
    // GET ALL DESTINATIONS
    // ---------------------------------------------------------

    /**
     * Returns all destinations.
     *
     * Each destination receives its current favorite status.
     */
    fun getDestinations(): List<Destination> {

        val remoteById = remoteDestinations.associateBy { it.id }
        val source = destinations.filter { it.id !in remoteDisabledDestinationIds }.map { remoteById[it.id] ?: it } + remoteDestinations.filter { remote -> destinations.none { it.id == remote.id } }
        return source.map { destination ->

            destination.copy(
                isFavorite =
                    favoriteIds.contains(
                        destination.id
                    )
            )
        }
    }


    // ---------------------------------------------------------
    // GET DESTINATION BY ID
    // ---------------------------------------------------------

    /**
     * Returns a single destination by ID.
     */
    fun getDestinationById(
        id: String
    ): Destination? {

        return destinations
            .find {

                it.id == id
            }
            ?.copy(

                isFavorite =
                    favoriteIds.contains(id)
            )
    }


    // ---------------------------------------------------------
    // GET DESTINATIONS BY CATEGORY
    // ---------------------------------------------------------

    /**
     * Returns destinations belonging
     * to a specific category.
     */
    fun getDestinationsByCategory(
        category: DestinationCategory
    ): List<Destination> {

        return destinations
            .filter {

                it.category == category
            }
            .map { destination ->

                destination.copy(

                    isFavorite =
                        favoriteIds.contains(
                            destination.id
                        )
                )
            }
    }


    // ---------------------------------------------------------
    // SEARCH DESTINATIONS
    // ---------------------------------------------------------

    /**
     * Searches destinations by:
     *
     * - Name
     * - Location
     * - Description
     */
    fun searchDestinations(
        query: String
    ): List<Destination> {

        if (query.isBlank()) {

            return getDestinations()
        }

        return getDestinations()

            .filter {

                it.name.contains(
                    query,
                    ignoreCase = true
                ) ||

                        it.location.contains(
                            query,
                            ignoreCase = true
                        ) ||

                        it.description.contains(
                            query,
                            ignoreCase = true
                        )
            }

            .map { destination ->

                destination.copy(

                    isFavorite =
                        favoriteIds.contains(
                            destination.id
                        )
                )
            }
    }


    // ---------------------------------------------------------
    // TOGGLE FAVORITE
    // ---------------------------------------------------------

    /**
     * Adds a destination to favorites
     * or removes it if it is already saved.
     */
    fun toggleFavorite(
        id: String
    ) {

        val nowFavorite = !favoriteIds.contains(id)
        if (nowFavorite) favoriteIds.add(id) else favoriteIds.remove(id)
        favoritesRef?.child(id)?.setValue(if (nowFavorite) true else null)
    }


    // ---------------------------------------------------------
    // CHECK FAVORITE
    // ---------------------------------------------------------

    /**
     * Returns true if the destination
     * is currently a favorite.
     */
    fun isFavorite(
        id: String
    ): Boolean {

        return favoriteIds.contains(id)
    }


    // ---------------------------------------------------------
    // GET FAVORITES
    // ---------------------------------------------------------

    /**
     * Returns all destinations
     * currently marked as favorites.
     */
    fun getFavoriteDestinations(): List<Destination> {

        return getDestinations()

            .filter {

                favoriteIds.contains(
                    it.id
                )
            }

            .map { destination ->

                destination.copy(

                    isFavorite = true
                )
            }
    }


    // ---------------------------------------------------------
    // CLEAR ALL FAVORITES
    // ---------------------------------------------------------

    /**
     * Removes all destinations
     * from the favorites list.
     */
    fun clearFavorites() {

        favoriteIds.clear()
    }
}
