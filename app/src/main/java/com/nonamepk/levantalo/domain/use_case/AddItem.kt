package com.nonamepk.levantalo.domain.use_case

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.nonamepk.levantalo.domain.repository.ItemRepository
import com.nonamepk.levantalo.domain.model.Item
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class AddItem(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(
        picture: Uri,
        location: LatLng,
        description: String?
    ) = flow {
        val _location = com.nonamepk.levantalo.domain.model.LatLng(
            latitude = location.latitude,
            longitude = location.longitude
        )

        repository.uploadImageToFbStorage(imageUri = picture).collect { uploadedImageUri ->
            val item = Item(uploadedImageUri.toString(), _location, description, createdDate = LocalDateTime.now().toString())
            repository.addItemToFirestore(item).collect { itemId ->
                emit(itemId)
            }
        }
    }
}