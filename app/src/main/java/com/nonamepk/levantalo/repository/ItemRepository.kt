package com.nonamepk.levantalo.repository

import android.net.Uri
import com.nonamepk.levantalo.model.Item
import com.nonamepk.levantalo.model.Response
import kotlinx.coroutines.flow.Flow

interface ItemRepository {

    fun getItemsFromFirestore(): Flow<Response<List<Item>>>

    suspend fun addItemToFirestore(item: Item): Flow<Response<String?>> // returns the id of the added item

    suspend fun uploadImageToFbStorage(imageUri: Uri): Flow<Uri?>
}