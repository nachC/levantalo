package com.nonamepk.levantalo.domain.utils

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.android.gms.maps.model.LatLng
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val EMPTY_IMAGE_URI = Uri.parse("file://dev/null")

fun getLocationByAddress(context: Context, strAddress: String?): LatLng? {
    val coder = Geocoder(context)
    try {
        val address = coder.getFromLocationName(strAddress, 5) ?: return null
        val location = address.first()
        return LatLng(location.latitude, location.longitude)
    } catch (e: Exception) {
        Log.e("getLocationByAddress", "error: $e")
    }
    return null
}

@ExperimentalPermissionsApi
@Composable
fun Permission(
    permissions: List<String> = emptyList(),
    rationale: String = "This permission is important for this app. Please grant the permission.",
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberMultiplePermissionsState(permissions)

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = {
            Rationale(
                text = rationale,
                onRequestPermission = { permissionState.launchMultiplePermissionRequest() }
            )
        },
        permissionsNotAvailableContent = permissionNotAvailableContent,
        content = content
    )
}

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = "Permission request")
        },
        text = {
            Text(text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Ok")
            }
        }
    )
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, executor)
    }
}

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)
