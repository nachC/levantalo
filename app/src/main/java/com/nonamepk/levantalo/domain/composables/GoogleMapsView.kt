package com.nonamepk.levantalo.domain.composables

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.nonamepk.levantalo.domain.model.Item

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapView(
    items: List<Item>
) {
    val context = LocalContext.current
    var selectedMarker by remember { mutableStateOf<Item?>(null)}

    /*Permission(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ),
        rationale = "When looking for items, it's easier to find them if you can see your position! Please, allow the app to have access to your location. Don't worry, we don't save any info that can identify you.",
        permissionNotAvailableContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("You can't use this feature without taking a picture and providing your location. Please go to Setting to provide the Camera/Location permission. Thanks!")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }
                ) {
                    Text("Open Settings")
                }
            }
        }
    ) {*/

        val barcelona = LatLng(41.390205, 2.154007)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(barcelona, 13f)
        }

        val locationPermissionsState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissionsState.allPermissionsGranted),
            onMapClick = { selectedMarker = null }
        ) {
            items.forEach { item ->
                item.location?.let {
                    Marker(
                        position = LatLng(
                            it.latitude!!,
                            it.longitude!!
                        ),
                        icon = if (selectedMarker != item) BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        onClick = {
                            selectedMarker = item
                            true
                        }
                    )
                }
            }
        }
        Button(onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.TopStart)) {
            Text("Enable location")
        }
        if (selectedMarker != null)
            Card(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(300.dp, 300.dp)
            ) {
                AsyncImage(model = selectedMarker?.picture, contentDescription = "item picture")
            }
    }
//    }
}
