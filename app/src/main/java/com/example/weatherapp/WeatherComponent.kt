package com.example.weatherapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherComp(searchUiState: WeatherInfo, searchViewModel: SearchViewModel) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = searchUiState.cityName,
                modifier = Modifier,
                fontSize = 30.sp
            )
            IconToggleButton(checked = searchUiState.favorite, onCheckedChange = {
                if (it) {
                    searchViewModel.addNewFavoriteCity(searchUiState.cityName)
                } else {
                    searchViewModel.removeFavoriteCity(searchUiState.cityName)
                }
            }) {
                Icon(
                    imageVector = if (searchUiState.favorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    }, contentDescription = null
                )
            }
        }
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Temperature",
                    modifier = Modifier,
                    fontSize = 18.sp
                )
                Text(
                    text = searchUiState.temperature,
                    modifier = Modifier,
                    fontSize = 16.sp
                )
                Text(
                    text = searchUiState.temperatureFeelsLike,
                    modifier = Modifier,
                    fontSize = 12.sp
                )
            }
            Image(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
                painter = painterResource(searchUiState.weatherIconInt),
                contentDescription = ""
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            )
            {
                Text(
                    text = "Weather",
                    modifier = Modifier,
                    fontSize = 18.sp
                )
                Text(
                    text = searchUiState.weatherMain,
                    modifier = Modifier,
                    fontSize = 16.sp
                )
                Text(
                    text = searchUiState.weatherDesc,
                    modifier = Modifier,
                    fontSize = 12.sp
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Wind",
                    modifier = Modifier,
                    fontSize = 18.sp
                )
                Text(
                    text = searchUiState.windSpeed,
                    modifier = Modifier,
                    fontSize = 16.sp
                )
                Text(
                    text = searchUiState.windDeg,
                    modifier = Modifier,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Show() {
    WeatherComp(searchUiState = WeatherInfo(), searchViewModel = SearchViewModel())
}