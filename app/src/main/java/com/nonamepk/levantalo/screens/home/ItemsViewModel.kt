package com.nonamepk.levantalo.screens.home

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
        picture: String?,
        location: LatLng,
        description: String?
    ) {
        _isItemAddedState.value = Response.Loading
        val _location = ItemLatLng(latitude = location.latitude, longitude = location.longitude)
        val item = Item(picture, _location, description, createdDate = LocalDateTime.now().toString())
        viewModelScope.launch {
            repository.addItemToFirestore(item).collect { response ->
                Log.d("ItemsViewModel", "addItem: $response")
                _isItemAddedState.value = response
            }
        }
    }
}