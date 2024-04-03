package com.example.weatherapp

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.App.Companion.minput
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
import java.io.BufferedReader
import kotlin.random.Random

class SearchViewModel : ViewModel() {
    private lateinit var weatherService: WeatherService
    private lateinit var weatherServiceGeo: WeatherServiceGeo

    private val _searchUiState = MutableStateFlow(WeatherInfo())
    val searchUiState: StateFlow<WeatherInfo> = _searchUiState.asStateFlow()

    val rt: MutableList<String> = mutableListOf()

    private val reader = BufferedReader(minput)
    private var line: String = ""

    init {
        Log.d("my", minput.toString())
        Log.d("my", reader.readLine().toString())
        while (reader.readLine().also {
                if (it != null) {
                    line = it
                }
            } != null) {
            rt.add(line)
        }
    }

    fun reInit() {
        if (prefs.all.keys.toMutableSet().isNotEmpty()) {
            val cities = prefs.all.keys.toMutableSet()
            if (!cities.contains(_searchUiState.value.cityName)) {
                getWeatherData(_searchUiState.value.cityName, true)
            }
        }
    }

    fun updateStartUi(location: Location) {
        Log.d("my", location.toString())
        getWeatherDataFromLatLong(location.latitude.toString(), location.longitude.toString())
    }

    fun addNewFavoriteCity(city: String) {
        prefs.edit().putBoolean(city, true).apply()
        updateUI {
            it.copy(favorite = true)
        }
    }

    fun removeFavoriteCity(city: String) {
        if (prefs.contains(city)) {
            prefs.edit().remove(city).apply()
        }
        reInit()
    }

    private fun getWeatherDataFromLatLong(lat: String, lon: String) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherServiceGeo =
                Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(WeatherServiceGeo::class.java)

            val weatherData = weatherServiceGeo.getWeather(lat, lon, API_KEY, "metric")
            withContext(Dispatchers.Main) {
                val city = weatherData.name
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

                updateUI {
                    WeatherInfo(
                        city,
                        temperature,
                        temperatureFeelsLike,
                        weatherMain,
                        weatherDesc,
                        weatherIcon,
                        weatherIconInt,
                        windSpeed,
                        windDeg,
                        favorite
                    )
                }
            }
        }
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

            updateUI {
                WeatherInfo(
                    city,
                    temperature,
                    temperatureFeelsLike,
                    weatherMain,
                    weatherDesc,
                    weatherIcon,
                    weatherIconInt,
                    windSpeed,
                    windDeg,
                    favorite
                )
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                weatherService =
                    Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
                        .create(WeatherService::class.java)

                val weatherData = weatherService.getWeather(city, API_KEY, "metric")
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

                    updateUI {
                        WeatherInfo(
                            city,
                            temperature,
                            temperatureFeelsLike,
                            weatherMain,
                            weatherDesc,
                            weatherIcon,
                            weatherIconInt,
                            windSpeed,
                            windDeg,
                            favorite
                        )
                    }
                }
            }
        }
    }

    private fun updateUI(
        update: (WeatherInfo) -> WeatherInfo
    ) {
        _searchUiState.update { currentState ->
            update.invoke(currentState)
        }
    }
}