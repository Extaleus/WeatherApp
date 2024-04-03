package com.example.weatherapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import java.util.function.Consumer

class MainActivity : ComponentActivity() {
    private val favoritesViewModel = FavoritesViewModel()
    private val searchViewModel = SearchViewModel()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }

            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            }

            else -> {
                // No location access granted.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionRequest.launch(
            arrayOf(
                ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
            )
        )

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

fun getLocation(context: Context, consumer: Consumer<Location>) {
    Log.d("my", "start get location")

    if (SDK_INT >= android.os.Build.VERSION_CODES.R) {
        Log.d("my", "SDK: $SDK_INT")

        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationPermissionCode = 2

        if (ActivityCompat.checkSelfPermission(
                context, ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("my", "PERMISSONS FAILED")
            ActivityCompat.requestPermissions(
                context as Activity, arrayOf(ACCESS_FINE_LOCATION), locationPermissionCode
            )
        } else {
            locationManager.getCurrentLocation(
                LocationManager.NETWORK_PROVIDER, null, context.mainExecutor, consumer
            )
        }

    } else {
        // Keep using the legacy code, such as LocationManager.requestSingleUpdate()
    }
}

@Composable
fun Search(
    searchViewModel: SearchViewModel, searchUiState: WeatherInfo, onNavigateToFavorites: () -> Unit
) {
    val context = LocalContext.current

    var firstRun by remember { mutableStateOf(true) }

    if (firstRun) {
        getLocation(context) { location ->
            searchViewModel.updateStartUi(location)
        }
        firstRun = false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        SearchBar(searchViewModel)

        WeatherComp(searchUiState, searchViewModel)

        Button(modifier = Modifier, onClick = {
            getLocation(context) { location ->
                searchViewModel.updateStartUi(location)
            }
        }) {
            Text("Refresh")
        }

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
