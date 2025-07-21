package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Airport::class], version = 1)
abstract class flightSearchDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao

    companion object {
        @Volatile
        private var Instance: flightSearchDatabase? = null

        fun getDatabase(context: Context): flightSearchDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    flightSearchDatabase::class.java,
                    "flight_search_database"
                )
                    .createFromAsset("database/flight_search.db")
                    .build()
                    .also { Instance = it }
            }
        }

    }
}