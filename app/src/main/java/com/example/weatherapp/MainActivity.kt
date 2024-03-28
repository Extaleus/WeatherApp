package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private val apiKey = "38aa59a66b09c58584737f4bc815aeed"
    private lateinit var weatherService: WeatherService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val weatherData = weatherService.getWeather("London", apiKey, "metric")
            withContext(Dispatchers.Main) {
//                updateUI(weatherData)
                val city = weatherData.name
                val temperature = "${weatherData.main.temp.toInt()}°C"
                Log.d("my", "need update ui, $city, $temperature")

                setContent {
                    WeatherAppTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Greeting(city, temperature)
                        }
                    }
                }
            }
        }
    }
}

//private fun updateUI(weatherData: WeatherData) {
//    findViewById<TextView>(R.id.textViewCity).text = weatherData.name
//    findViewById<TextView>(R.id.textViewTemperature).text =
//        "${weatherData.main.temp.toInt()}°C"
//    val iconUrl = "https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png"
//    Glide.with(this)
//        .load(iconUrl)
//        .into(findViewById(R.id.imageViewWeatherIcon))
//}

@Composable
fun Greeting(city: String, temperature: String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar()
        Image(
            painterResource(R.drawable.day),
            contentDescription = "",
            modifier = Modifier
        )
        Text(
            text = city,
            modifier = modifier
        )
        Text(
            text = temperature,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppTheme {
        Greeting("London", "45°C")
    }
}



