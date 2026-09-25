package com.kigz.javillasafarihub.planner

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kigz.javillasafarihub.data.model.SavedTrip

@Database(
    entities = [SavedTrip::class],
    version = 1,
    exportSchema = false
)
abstract class SafariDatabase : RoomDatabase() {

    abstract fun savedTripDao(): SavedTripDao

    companion object {

        @Volatile
        private var INSTANCE: SafariDatabase? = null

        fun getDatabase(
            context: Context
        ): SafariDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        SafariDatabase::class.java,
                        "javilla_safari_hub_database"
                    )
                        .build()

                INSTANCE = instance

                instance
            }
        }
    }
}