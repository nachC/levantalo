package com.nonamepk.levantalo.presentation.screens.home

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nonamepk.levantalo.domain.model.Item
import com.nonamepk.levantalo.domain.model.Response
import com.nonamepk.levantalo.domain.use_case.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val useCases: UseCases
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
            useCases.getItems().collect { response ->
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
        viewModelScope.launch {
            useCases.addItem(picture, location, description).collect {
                _isItemAddedState.value = it
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
