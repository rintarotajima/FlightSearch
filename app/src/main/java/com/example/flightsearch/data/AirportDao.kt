package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE :query || '%' OR name LIKE :query || '%' ORDER BY passengers DESC")
    suspend fun searchAirports(query: String): Flow<List<Airport>>
}