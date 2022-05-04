package com.nonamepk.levantalo.composables

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.nonamepk.levantalo.model.Item
import com.nonamepk.levantalo.utils.getLocationByAddress

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapView(
    items: List<Item>
) {
    Log.d("GoogleMapView composable", "GoogleMapView: items: $items")
    val locationFromAddress = getLocationByAddress(context = LocalContext.current, "Travessera de Gràcia, 177, Barcelona")
    Log.d("GoogleMapView", "Location from address: $locationFromAddress")

    val barcelona = LatLng(items.first().location?.latitude!!, items.first().location?.longitude!!)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(barcelona, 13f)
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = locationPermissionsState.allPermissionsGranted)
    ) {

        items.forEach { item ->
            item.location?.let {
                Marker(
                    position = LatLng(
                        it.latitude!!,
                        it.longitude!!
                    )
                )
            }
        }
    }
}
