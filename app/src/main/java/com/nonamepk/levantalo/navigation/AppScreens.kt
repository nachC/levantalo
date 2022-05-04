package com.nonamepk.levantalo.navigation

import com.nonamepk.levantalo.screens.takeoutitem.CameraPreview

enum class AppScreens {
    HomeScreen,
    StartScreen,
    TakeOutItemScreen,
    CameraPreview,
    CameraCapture;

    companion object {
        fun fromRoute(route: String?): AppScreens
        = when(route?.substringBefore("/")) {
            HomeScreen.name -> HomeScreen
            StartScreen.name -> StartScreen
            TakeOutItemScreen.name -> TakeOutItemScreen
            CameraPreview.name -> CameraPreview
            CameraCapture.name -> CameraCapture
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}