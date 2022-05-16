package com.nonamepk.levantalo.screens.home

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nonamepk.levantalo.model.Item
import com.nonamepk.levantalo.model.LatLng as ItemLatLng
import com.nonamepk.levantalo.model.Response
import com.nonamepk.levantalo.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    private val _itemsState = mutableStateOf<Response<List<Item>>>(Response.Loading)
    val itemsState: State<Response<List<Item>>> = _itemsState

    private val _isItemAddedState = mutableStateOf<Response<String?>>(Response.Success(null))
    val isItemAddedState: State<Response<String?>> = _isItemAddedState

    private val _takeOutUiState = mutableStateOf<TakeOutUiState>(TakeOutUiState.MainView)
    val takeOutUiState = _takeOutUiState

    init {
        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {
            repository.getItemsFromFirestore().collect { response ->
                Log.d("ItemsViewModel", "getItems: $response")
                _itemsState.value = response
            }
        }
    }

    fun addItem(
        picture: Uri,
        location: LatLng,
        description: String?
    ) {
        _takeOutUiState.value = TakeOutUiState.Loading
        val _location = ItemLatLng(latitude = location.latitude, longitude = location.longitude)
        viewModelScope.launch {
            repository.uploadImageToFbStorage(imageUri = picture).collect { uploadedImageUri ->
//                Log.d("ItemsViewModel", "uploadedPicture: $uploadedImageUri")
                val item = Item(uploadedImageUri.toString(), _location, description, createdDate = LocalDateTime.now().toString())
                repository.addItemToFirestore(item).collect { itemId ->
//                    Log.d("ItemsViewModel", "addItem id: $itemId")
                    _isItemAddedState.value = itemId
                    _takeOutUiState.value = TakeOutUiState.MainView
                }
            }
        }
    }

    fun openCameraView() {
        _takeOutUiState.value = TakeOutUiState.CameraView
    }

    fun openGalleryView() {
        _takeOutUiState.value = TakeOutUiState.GalleryView
    }

    fun openMainView() {
        _takeOutUiState.value = TakeOutUiState.MainView
    }
}

sealed class TakeOutUiState {
    object MainView: TakeOutUiState()
    object CameraView: TakeOutUiState()
    object GalleryView: TakeOutUiState()
    object Error: TakeOutUiState()
    object Loading: TakeOutUiState()
}
