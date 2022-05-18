package com.nonamepk.levantalo.screens.takeoutitem

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.nonamepk.levantalo.composables.CustomTextField
import com.nonamepk.levantalo.model.Response
import com.nonamepk.levantalo.navigation.AppScreens
import com.nonamepk.levantalo.screens.home.ItemsViewModel
import com.nonamepk.levantalo.screens.home.TakeOutUiState
import com.nonamepk.levantalo.utils.EMPTY_IMAGE_URI
import com.nonamepk.levantalo.utils.Permission
import com.nonamepk.levantalo.utils.executor
import com.nonamepk.levantalo.utils.getCameraProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TakeOutItemScreen(
    navController: NavController,
    viewModel: ItemsViewModel = hiltViewModel(),
    fusedLocationClient: FusedLocationProviderClient
) {
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    val context = LocalContext.current

    Permission(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ),
        rationale = "When gifting items, you'll have to take a picture of them. Also, you location is automatically added. Please, provide access to both your camera and location to use this feature. Don't worry, we don't save any info that can identify you.",
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
    ) {
        when (viewModel.takeOutUiState.value) {
            is TakeOutUiState.MainView -> CreateBody(
                imageUri = imageUri,
                viewModel = viewModel,
                onTakePictureClicked = { viewModel.openCameraView() },
                onAddItemClicked = { description ->
                    addItem(imageUri = imageUri, description = description, fusedLocationClient = fusedLocationClient, viewModel = viewModel)
                }
            )
            is TakeOutUiState.CameraView -> CameraCaptureScreen(
                viewModel = viewModel,
            ) { file ->
                Log.d("TakeOut", "imageUri from Camera: ${file.toUri()}")
                imageUri = file.toUri()
            }
            is TakeOutUiState.GalleryView -> GallerySelectionScreen(
                viewModel = viewModel
            ) {  uri ->
                Log.d("TakeOut", "imageUri from Gallery: $uri")
                imageUri = uri
            }
            is TakeOutUiState.Error -> Text("Error")
            is TakeOutUiState.Loading -> CircularProgressIndicator()
        }

        when (val addItemResponse = viewModel.isItemAddedState.value) {
            is Response.Loading -> Unit
            is Response.Error -> Toast.makeText(context, "Error adding item. Please, try again", Toast.LENGTH_SHORT).show()
            is Response.Success -> {
                Log.d("TakeOutItemScreen", "Response: $addItemResponse")
                if (addItemResponse.data != null) {
                    Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show()
                    navController.popBackStack(route = AppScreens.StartScreen.name, inclusive = false)
                }
            }
        }
    }
}

@Composable
private fun CreateBody(
    imageUri: Uri,
    onTakePictureClicked: () -> Unit,
    onAddItemClicked: (description: String) -> Unit,
    viewModel: ItemsViewModel,
) {

    var description by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Row {
                Button(
                    onClick = onTakePictureClicked,
                    modifier = Modifier.padding(20.dp)
                )
                {
                    Text(text = "Take picture")
                }
            }
            if (imageUri != EMPTY_IMAGE_URI)
                AsyncImage(
                    model = imageUri,
                    contentDescription = "item picture",
                )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextField(
                    text = description,
                    label = "Description (optional)",
                    hint = "e.g. Sofa, chair, table...",
                    onTextChange = {
                        description = it
                    },
                    maxLines = 1
                )
            }

            if (!loading) Button(
                onClick = { onAddItemClicked(description) },
                modifier = Modifier.padding(20.dp)
            )
            {
                Text(text = "Add Item")
            }
            else CircularProgressIndicator()
        }
    }
}

@SuppressLint("MissingPermission")
private fun addItem(
    imageUri: Uri,
    description: String?,
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: ItemsViewModel) {

    var location: LatLng? = null
    val locationRequest = LocationRequest.create().apply {
        numUpdates = 1
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0.lastLocation.run {
                location = LatLng(this.latitude, this.longitude)
            }
            fusedLocationClient.removeLocationUpdates(this)
        }
    }

    fusedLocationClient.lastLocation
        .addOnSuccessListener {
            Log.d("OnSuccessListener", "Location: $it")
            if (it != null) {
                location = LatLng(it.latitude, it.longitude)

            } else {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }
            location?.let { location -> viewModel.addItem(imageUri, location, description) }
        }
        .addOnFailureListener {
            Log.d("OnFailureListener", "Location: ${it.message}")
        }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    onUseCase: (UseCase) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // CameraX Preview UseCase
            onUseCase(Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                })
            previewView
        }
    )
}

@Composable
fun CameraCaptureScreen(
    modifier: Modifier = Modifier,
    viewModel: ItemsViewModel,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onImageFile: (File) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
    val imageCaptureUseCase by remember {
        mutableStateOf(
            ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
        )
    }

    Box(modifier = modifier) {
        Box(Modifier.fillMaxSize()) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onUseCase = {
                    previewUseCase = it
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.White.copy(alpha = 0.3f)),
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            onImageFile(imageCaptureUseCase.takePicture(context.executor))
                            viewModel.openMainView()
                        }
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Take picture button",
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.openGalleryView() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Take picture button",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

        }

        LaunchedEffect(previewUseCase) {
            val cameraProvider = context.getCameraProvider()
            try {
                // Must unbind the use-cases before rebinding them.
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                )
            } catch (ex: Exception) {
                Log.e("CameraCapture", "Failed to bind camera use cases", ex)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GallerySelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: ItemsViewModel,
    onImageUri: (Uri) -> Unit = { }
) {

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            onImageUri(uri ?: EMPTY_IMAGE_URI)
            viewModel.openMainView()
        }
    )

    @Composable
    fun LaunchGallery() {
        SideEffect {
            launcher.launch("image/*")
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Permission(
            permissions = listOf(Manifest.permission.ACCESS_MEDIA_LOCATION),
            rationale = "We need access to your photo gallery to pick a photo from it.",
            permissionNotAvailableContent = {
                Column(modifier) {
                    Text("No permission to access gallery")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                })
                            }
                        ) {
                            Text("Open Settings")
                        }
                        // If they don't want to grant permissions, this button will result in going back
                        Button(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                onImageUri(EMPTY_IMAGE_URI)
                            }
                        ) {
                            Text("Use Camera")
                        }
                    }
                }
            },
        ) {
            LaunchGallery()
        }
    } else {
        LaunchGallery()
    }
}

suspend fun ImageCapture.takePicture(executor: Executor): File {
    val photoFile = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            File.createTempFile("image", "jpg")
        }.getOrElse { ex ->
            Log.e("TakePicture", "Failed to create temporary file", ex)
            File("/dev/null")
        }
    }

    return suspendCoroutine { continuation ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                continuation.resume(photoFile)
            }

            override fun onError(ex: ImageCaptureException) {
                Log.e("TakePicture", "Image capture failed", ex)
                continuation.resumeWithException(ex)
            }
        })
    }
}