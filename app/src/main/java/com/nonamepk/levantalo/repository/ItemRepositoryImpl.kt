package com.nonamepk.levantalo.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.nonamepk.levantalo.model.Item
import com.nonamepk.levantalo.model.Response
import com.nonamepk.levantalo.model.Response.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemsRef: CollectionReference
): ItemRepository {

    override fun getItemsFromFirestore() = callbackFlow {
        val snapshotListener = itemsRef.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                val items = snapshot.toObjects(Item::class.java)
                snapshot.forEach {
                    Log.d("ItemRepoImpl", "getItemsFromFirestore: document id ${it.id} and data: ${it.data}")
                }
                Success(items)
            } else {
                Error(e?.message ?: e.toString())
            }
            trySend(response).isSuccess
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun addItemToFirestore(item: Item): Flow<Response<String?>> = flow {
        try {
            emit(Loading)
            val itemId = itemsRef.document().id
            itemsRef.document(itemId).set(item).await()
            emit(Success(itemId))
        } catch (e: Exception) {
            emit(Error(e.message ?: e.toString()))
        }
    }
}