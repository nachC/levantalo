package com.nonamepk.levantalo.screens.home

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nonamepk.levantalo.composables.GoogleMapView
import com.nonamepk.levantalo.model.Response.*

@Composable
fun HomeScreen(navController: NavController, viewModel: ItemsViewModel = hiltViewModel()) {
    Surface {
        when(val itemsResponse = viewModel.itemsState.value) {
            is Loading -> CircularProgressIndicator()
            is Success -> GoogleMapView(items = itemsResponse.data)
            is Error -> Text(text = "Error loading map")
        }

    }
}