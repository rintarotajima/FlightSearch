package com.example.flightsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.FlightSearchViewModel
import com.example.flightsearch.ui.theme.FlightSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlightSearchTheme {
                FlightSearchApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.Factory)
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val airportSuggestions by viewModel.airportSuggestions.collectAsState()

    Scaffold(
        topBar = { FlightSearchAppTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FlightSearchInput(
                searchQuery = searchQuery,
                onQueryChange = { newQuery ->
                    viewModel.onSearchQueryChange(newQuery)
                },
                airportSuggestions = airportSuggestions,
                onAirportSelected = { },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchInput(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    airportSuggestions: List<Airport>,
    onAirportSelected: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    // 検索バーの展開状態を保持
    var expanded by rememberSaveable { mutableStateOf(false)}

    Box(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it},
                    placeholder = {Text("空港名またはIATAコードを入力")},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it}
        ) {
            if (airportSuggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(airportSuggestions, key = { airport -> airport.id }) { airport ->
                        ListItem(
                            headlineContent = { Text(text = airport.name) },
                            supportingContent = { Text(text = airport.iataCode) },
                            modifier = Modifier.clickable {
                                onAirportSelected(airport)
                                expanded = false
                            },

                        )

                    }
                }
            } else if (searchQuery.isNotBlank()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "「$searchQuery」に一致する空港が見つかりませんでした。")
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun FlightSearchInputPreview() {
    FlightSearchInput(
       searchQuery = "東京",
        onQueryChange = {},
        airportSuggestions = listOf(
            Airport(1, "羽田空港", "TYO", 1000),
            Airport(2, "羽田空港", "TYO", 1000),
        ),
        onAirportSelected = {}
    )
}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewFlightSearchApp() {
    FlightSearchApp()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchAppTopBar() {
    TopAppBar(
        title = { Text(text = "Flight Search") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}


