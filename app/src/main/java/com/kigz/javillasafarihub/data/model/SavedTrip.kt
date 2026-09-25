package com.kigz.javillasafarihub.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_trips")
data class SavedTrip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tripName: String = "",
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val travelers: String = "1",
    val activities: String = "",
    val transport: String = "",
    val estimatedBudget: Double = 0.0,
    val notes: String = ""
)
