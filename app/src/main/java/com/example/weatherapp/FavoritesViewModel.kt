package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.App.Companion.prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

class FavoritesViewModel : ViewModel() {
    private val apiKey = "38aa59a66b09c58584737f4bc815aeed"
    private lateinit var weatherService: WeatherService

    private val _favoritesUiState = MutableStateFlow(Cities())
    val favoritesUiState: StateFlow<Cities> = _favoritesUiState.asStateFlow()

    private var cities: MutableSet<String> = prefs.all.keys.toMutableSet()

//    init {
//        cities.forEach {
//            getWeatherData(it, true)
//        }
//    }

    fun reInit() {
        _favoritesUiState.update {
            it.apply {
                snapshotStateList.clear()
            }
        }
        cities = prefs.all.keys
        cities.forEach {
            getWeatherData(it, true)
        }
    }

    fun removeFavoriteCity(item: WeatherInfo) {
        if (prefs.contains(item.cityName)) {
            prefs.edit().remove(item.cityName).apply()
            _favoritesUiState.update {
                it.apply {
                    snapshotStateList.remove(item)
                }
            }
        }
    }

    private fun getWeatherData(city: String, local: Boolean = false) {
        if (local) {
            val temperature = " ${Random.nextInt(1, 30)}°C"
            _favoritesUiState.update {
                it.apply {
                    snapshotStateList.add(
                        WeatherInfo(
                            city,
                            temperature
                        )
                    )
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                weatherService =
                    Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                        .create(WeatherService::class.java)
                val weatherData = weatherService.getWeather(city, apiKey, "metric")
                withContext(Dispatchers.Main) {
                    val temperature = weatherData.main.temp.toInt().toString()
                    _favoritesUiState.update {
                        it.apply {
                            snapshotStateList.add(
                                WeatherInfo(
                                    city,
                                    temperature
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}