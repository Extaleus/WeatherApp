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

class SearchViewModel : ViewModel() {
    private val apiKey = "38aa59a66b09c58584737f4bc815aeed"
    private lateinit var weatherService: WeatherService

    private val _searchUiState = MutableStateFlow(WeatherInfo())
    val searchUiState: StateFlow<WeatherInfo> = _searchUiState.asStateFlow()

    fun reInit(){
        if (prefs.all.keys.toMutableSet().isNotEmpty()){
            val cities = prefs.all.keys.toMutableSet()
            if (!cities.contains(_searchUiState.value.cityName)) {
                getWeatherData(_searchUiState.value.cityName, true)
            }
        }
    }

    fun addNewFavoriteCity(city: String) {
        prefs.edit().putBoolean(city, true).apply()
        updateUI(
            _searchUiState.value.cityName,
            _searchUiState.value.temperature,
            _searchUiState.value.temperatureFeelsLike,
            _searchUiState.value.weatherMain,
            _searchUiState.value.weatherDesc,
            _searchUiState.value.weatherIconInt,
            _searchUiState.value.windSpeed,
            _searchUiState.value.windDeg,
            true
        )
    }

    fun removeFavoriteCity(city: String) {
        if (prefs.contains(city)) {
            prefs.edit().remove(city).apply()
        }
        reInit()
    }

    fun getWeatherData(city: String, local: Boolean = false) {
        if (local) {
            val temperature = "${Random.nextInt(1, 30)}"
            val temperatureFeelsLike = "${Random.nextInt(1, 30)}"
            val weatherMain = "Rain"
            val weatherDesc = "Desc: Little rain"
            val weatherIcon = "09d"
            val weatherIconInt = R.drawable._09d
            val windSpeed = "Speed: 10 mph"
            val windDeg = "Direction: 154°"

            val favorite = prefs.contains(city)

            updateUI(
                city,
                temperature,
                temperatureFeelsLike,
                weatherMain,
                weatherDesc,
                weatherIconInt,
                windSpeed,
                windDeg,
                favorite
            )
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                weatherService =
                    Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                        .create(WeatherService::class.java)

                val weatherData = weatherService.getWeather(city, apiKey, "metric")
                withContext(Dispatchers.Main) {
                    val temperature = weatherData.main.temp.toInt().toString()
                    val temperatureFeelsLike = weatherData.main.feels_like.toInt().toString()
                    val weatherMain = weatherData.weather[0].main
                    val weatherDesc = weatherData.weather[0].description
                    val weatherIcon = weatherData.weather[0].icon
                    val weatherIconInt = when (weatherIcon) {
                        "01d" -> R.drawable._01d
                        "02d" -> R.drawable._02d
                        "03d" -> R.drawable._03d
                        "04d" -> R.drawable._04d
                        "09d" -> R.drawable._09d
                        "10d" -> R.drawable._10d
                        "11d" -> R.drawable._11d
                        "13d" -> R.drawable._13d
                        "50d" -> R.drawable._50d
                        else -> R.drawable.ic_launcher_foreground
                    }
                    val windSpeed = weatherData.wind.speed.toString()
                    val windDeg = weatherData.wind.deg.toString()

                    val favorite = prefs.contains(city)

                    updateUI(
                        city,
                        temperature,
                        temperatureFeelsLike,
                        weatherMain,
                        weatherDesc,
                        weatherIconInt,
                        windSpeed,
                        windDeg,
                        favorite
                    )
                }
            }
        }
    }

    private fun updateUI(
        city: String,
        temperature: String,
        temperatureFeelsLike: String,
        weatherMain: String,
        weatherDesc: String,
        weatherIconInt: Int,
        windSpeed: String,
        windDeg: String,
        favorite: Boolean
    ) {
        _searchUiState.update { currentState ->
            currentState.copy(
                cityName = city,
                temperature = temperature,
                temperatureFeelsLike = temperatureFeelsLike,
                weatherMain = weatherMain,
                weatherDesc = weatherDesc,
                weatherIconInt = weatherIconInt,
                windSpeed = windSpeed,
                windDeg = windDeg,
                favorite = favorite
            )
        }
    }
}