package com.nonamepk.levantalo.domain.repository

import android.net.Uri
import com.nonamepk.levantalo.domain.model.Item
import com.nonamepk.levantalo.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface ItemRepository {

    fun getItemsFromFirestore(): Flow<Response<List<Item>>>

    suspend fun addItemToFirestore(item: Item): Flow<Response<String?>> // returns the id of the added item

    suspend fun uploadImageToFbStorage(imageUri: Uri): Flow<Uri?>
}