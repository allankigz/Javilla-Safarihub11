package com.kigz.javillasafarihub.planner

import com.kigz.javillasafarihub.data.model.SavedTrip
import kotlinx.coroutines.flow.Flow

class TripRepository(
    private val dao: SavedTripDao
) {

    fun getTrips(): Flow<List<SavedTrip>> {

        return dao.getAllTrips()
    }

    suspend fun saveTrip(
        trip: SavedTrip
    ) {

        dao.insertTrip(trip)
    }

    suspend fun deleteTrip(
        trip: SavedTrip
    ) {

        dao.deleteTrip(trip)
    }

    suspend fun clearTrips() {

        dao.deleteAllTrips()
    }
}