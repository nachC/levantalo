package com.nonamepk.levantalo.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nonamepk.levantalo.repository.ItemRepository
import com.nonamepk.levantalo.repository.ItemRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    fun provideItemsCollectionRef(db: FirebaseFirestore) = db.collection("items")

    @Provides
    fun provideItemRepository(itemsRef: CollectionReference, storage: StorageReference): ItemRepository = ItemRepositoryImpl(itemsRef, storage)

    @Provides
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    fun provideStorageRef(storage: FirebaseStorage) = storage.reference
}