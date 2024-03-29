package com.example.weatherapp

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

    fun getWeatherData(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherService = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)

            val weatherData = weatherService.getWeather(city, apiKey, "metric")
            withContext(Dispatchers.Main) {
                val temperature = "${weatherData.main.temp.toInt()}°C"
                updateUI(city, temperature)
                Log.d("my", "$city, $temperature")
            }
        }
    }

    private fun updateUI(city: String, temperature: String) {
        _uiState.update { currentState ->
            currentState.copy(
                city = city,
                temperature = temperature
            )
        }
    }

}