package com.example.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceGeo {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): WeatherData
}
