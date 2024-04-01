package com.example.weatherapp

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

@Composable
fun SearchBar(
    mainViewModel: MainViewModel
) {
    var currentFieldText by remember { mutableStateOf("") }

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(interactionSource = interactionSource, indication = null, onClick = {
                    expanded = false
                })
        ) {
            TextField(modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
                value = currentFieldText,
                onValueChange = {
                    currentFieldText = it
                    expanded = true
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = null
                    )
                },
                placeholder = { Text(stringResource(R.string.placeholder_search)) },
                textStyle = TextStyle(
                    color = Color.Black, fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = if (expanded) {
                                Icons.Rounded.KeyboardArrowUp
                            } else {
                                Icons.Rounded.KeyboardArrowDown
                            },
                            contentDescription = "arrow",
                            tint = Color.Black
                        )
                    }
                })
            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 180.dp),
                    ) {
                        if (currentFieldText.isNotEmpty()) {
                            items(rusCities.filter {
                                it.lowercase()
                                    .contains(currentFieldText.lowercase()) || it.lowercase()
                                    .contains("others")
                            }.sorted()) {
                                ItemsCategory(mainViewModel, title = it) { title ->
                                    currentFieldText = title
                                    expanded = false
                                }
                            }
                        } else {
                            items(
                                rusCities.sorted()
                            ) {
                                ItemsCategory(mainViewModel, title = it) { title ->
                                    currentFieldText = title
                                    expanded = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemsCategory(
    mainViewModel: MainViewModel,
    title: String, onSelect: (String) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onSelect(title)
            Log.d("my", "Selected city: $title")
            mainViewModel.getWeatherData(title)
        }
        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)) {
        Text(text = title, fontSize = 16.sp)
    }
}
