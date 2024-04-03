package com.example.weatherapp

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun FavoritesScreen(favoritesViewModel: FavoritesViewModel, favoritesUiState: Cities) {
    LazyColumn(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = favoritesUiState.snapshotStateList, itemContent = { item ->
            FavoriteItem(item){
                favoritesViewModel.removeFavoriteCity(item)
            }
        })
    }
}

@Composable
fun FavoriteItem(
    item: WeatherInfo,
    favoriteClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.cityName,
            modifier = Modifier,
            fontSize = 30.sp,
        )
        Text(
            text = item.temperature,
            modifier = Modifier,
            fontSize = 30.sp,
        )
        IconButton(onClick = favoriteClick) {
            Icon(
                imageVector = Icons.Default.Favorite, contentDescription = null
            )
        }
    }
}
