package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    private val favoritesViewModel = FavoritesViewModel()
    private val searchViewModel = SearchViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainView(searchViewModel, favoritesViewModel)
                }
            }
        }
    }
}

@Composable
fun MainView(
    searchViewModel: SearchViewModel, favoritesViewModel: FavoritesViewModel
) {
    val searchUiState by searchViewModel.searchUiState.collectAsStateWithLifecycle()
    val favoritesUiState by favoritesViewModel.favoritesUiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    NavHost(navController, startDestination = "search") {
        composable("search") {
            Search(searchViewModel, searchUiState, onNavigateToFavorites = {
                favoritesViewModel.reInit()
                navController.navigate("favorites")
            })
        }
        composable("favorites") {
            Favorites(favoritesViewModel, favoritesUiState, onNavigateToSearch = {
                searchViewModel.reInit()
                navController.navigate("search")
            })
        }
    }
}

@Composable
fun Search(
    searchViewModel: SearchViewModel, searchUiState: WeatherInfo, onNavigateToFavorites: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        SearchBar(searchViewModel)

        WeatherComp(searchUiState, searchViewModel)

        Button(modifier = Modifier, onClick = { onNavigateToFavorites() }) {
            Text("Go to Favorites")
        }
    }
}

@Composable
fun Favorites(
    favoritesViewModel: FavoritesViewModel, favoritesUiState: Cities, onNavigateToSearch: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(16.dp)
    ) {
        FavoritesScreen(favoritesViewModel, favoritesUiState)

        Button(onClick = { onNavigateToSearch() }) {
            Text("Go to Search")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppTheme {
        MainView(searchViewModel = SearchViewModel(), favoritesViewModel = FavoritesViewModel())
    }
}
