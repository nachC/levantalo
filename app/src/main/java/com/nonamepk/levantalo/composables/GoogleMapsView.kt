package com.nonamepk.levantalo.composables

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.nonamepk.levantalo.utils.Permission
import com.nonamepk.levantalo.utils.getLocationByAddress

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapView(
    items: List<Item>
) {
    val context = LocalContext.current

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

        val barcelona = LatLng(items.first().location?.latitude!!, items.first().location?.longitude!!)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(barcelona, 13f)
        }

        val locationPermissionsState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
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
//    }
}
