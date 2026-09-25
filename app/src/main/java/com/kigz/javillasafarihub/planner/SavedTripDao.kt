package com.kigz.javillasafarihub.planner

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kigz.javillasafarihub.data.model.SavedTrip
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedTripDao {

    @Insert
    suspend fun insertTrip(trip: SavedTrip)

    @Delete
    suspend fun deleteTrip(trip: SavedTrip)

    @Query("SELECT * FROM saved_trips ORDER BY id DESC")
    fun getAllTrips(): Flow<List<SavedTrip>>

    @Query("DELETE FROM saved_trips")
    suspend fun deleteAllTrips()
}