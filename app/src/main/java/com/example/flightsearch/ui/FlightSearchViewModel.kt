package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.AirportDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.emptyList

class FlightSearchViewModel(private val airportDao: AirportDao) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val airportSuggestions: StateFlow<List<Airport>> = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                MutableStateFlow(emptyList())
            } else {
                airportDao.searchAirports(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

//  FlightSearchViewModelをインスタンス化するためのFactoryを定義
//  AirportDaoがコンストラクタで必要なため、カスタムFactoryを定義
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // ApplicationContextを取得
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                // Applicationクラス経由で、AirportDaoを取得
                val airportDao = application.database.airportDao()
                // AirportDaoを渡してFlightSearchViewModelをインスタンス化
                FlightSearchViewModel(airportDao)
            }
        }
    }

}