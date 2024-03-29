package com.example.weatherapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
@Composable
fun WeatherComp(uiState: WeatherInfo){
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier,
        ) {
            Text(
                text = uiState.city,
                modifier = Modifier,
                fontSize = 24.sp
            )
            Text(
                text = uiState.temperature,
                modifier = Modifier,
                fontSize = 20.sp
            )
        }
        Image(
            painterResource(R.drawable.day),
            contentDescription = "",
            modifier = Modifier,
        )
    }
}
