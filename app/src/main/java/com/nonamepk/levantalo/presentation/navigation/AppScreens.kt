package com.nonamepk.levantalo.presentation.navigation

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