package com.nonamepk.levantalo.repository

import com.nonamepk.levantalo.model.Item
import com.nonamepk.levantalo.model.Response
import kotlinx.coroutines.flow.Flow

interface ItemRepository {

    fun getItemsFromFirestore(): Flow<Response<List<Item>>>

    suspend fun addItemToFirestore(item: Item): Flow<Response<String?>> // returns the id of the added item
}