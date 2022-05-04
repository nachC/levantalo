package com.nonamepk.levantalo.model

data class Item(
    val picture: String? = null,
    val location: LatLng? = null,
    val description: String? = null,
    val createdDate: String? = null
)

data class LatLng(
    val latitude: Double? = null,
    val longitude: Double? = null
)