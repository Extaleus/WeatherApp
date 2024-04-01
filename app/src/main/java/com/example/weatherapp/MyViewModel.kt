package com.example.weatherapp

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {
    private val apiKey = "38aa59a66b09c58584737f4bc815aeed"
    private lateinit var weatherService: WeatherService

    private val _uiState = MutableStateFlow(WeatherInfo())
    val uiState: StateFlow<WeatherInfo> = _uiState.asStateFlow()

    private val _favoriteState = MutableStateFlow(FavoriteInfo())
    val favoriteState: StateFlow<FavoriteInfo> = _favoriteState.asStateFlow()

    private lateinit var prefs: SharedPreferences

    fun setSharedPreferences(preferences: SharedPreferences) {
        prefs = preferences
        clearSharedPreferences()
    }

    private fun clearSharedPreferences() {
//        prefs.edit().clear().apply()
        prefs.edit().remove("None").apply()
        checkSharedPreferences()
    }

    fun addNewFavoriteCity(city: String) {
        prefs.edit().putBoolean(city, true).apply()
        updateFavorite(city, true)
        checkSharedPreferences()
    }

    fun removeFavoriteCity(city: String) {
        if (prefs.contains(city)) {
            prefs.edit().remove(city).apply()
            updateFavorite(city, false)
        }
        checkSharedPreferences()
    }

    private fun checkSharedPreferences() {
        for (entry in prefs.all) {
            Log.d("my", entry.toString())
        }
    }

//    fun getFavoriteCities() {
//        if(prefs.contains("0")){
//            // Получаем число из настроек
//            counter = prefs.getInt(APP_PREFERENCES_COUNTER, 0)
//            // Выводим на экран данные из настроек
//            infoTextView.text = "Я насчитал $counter ворон"
//        }
//    }

    fun getWeatherData(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherService = Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
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

                updateFavorite(
                    city, favorite
                )

                updateUI(
                    temperature,
                    temperatureFeelsLike,
                    weatherMain,
                    weatherDesc,
                    weatherIconInt,
                    windSpeed,
                    windDeg
                )
                Log.d(
                    "my",
                    "$city, $temperature, $temperatureFeelsLike, $weatherMain," +
                            "$weatherDesc, $weatherIcon, $windSpeed, $windDeg"
                )
            }
        }
    }

    private fun updateUI(
        temperature: String,
        temperatureFeelsLike: String,
        weatherMain: String,
        weatherDesc: String,
        weatherIconInt: Int,
        windSpeed: String,
        windDeg: String,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                temperature = "Nature: $temperature°C",
                temperatureFeelsLike = "Feels Like: $temperatureFeelsLike°C",
                weatherMain = weatherMain,
                weatherDesc = "Desc: $weatherDesc",
                weatherIconInt = weatherIconInt,
                windSpeed = "Speed: $windSpeed mph",
                windDeg = "Direction: $windDeg°"
            )
        }
    }

    private fun updateFavorite(
        city: String, favorite: Boolean
    ) {
        _favoriteState.update { currentState ->
            currentState.copy(
                city = city,
                favorite = favorite
            )
        }
    }
}