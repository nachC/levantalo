package com.nonamepk.levantalo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.nonamepk.levantalo.screens.home.HomeScreen
import com.nonamepk.levantalo.screens.start.StartScreen
import com.nonamepk.levantalo.screens.takeoutitem.CameraCaptureScreen
import com.nonamepk.levantalo.screens.takeoutitem.CameraPreview
import com.nonamepk.levantalo.screens.takeoutitem.TakeOutItemScreen

@Composable
fun AppNavigation(
    fusedLocationClient: FusedLocationProviderClient
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController, 
        startDestination = AppScreens.StartScreen.name
    ) {
        composable(route = AppScreens.HomeScreen.name) {
            HomeScreen(navController = navController)
        }
        composable(route = AppScreens.StartScreen.name) {
            StartScreen(
                onSearchItemsClick = { navController.navigate(route = AppScreens.HomeScreen.name)},
                onTakeOutItemsClick = { navController.navigate(route = AppScreens.TakeOutItemScreen.name) }
            )
        }
        composable(route = AppScreens.TakeOutItemScreen.name) {
            TakeOutItemScreen(navController = navController, fusedLocationClient = fusedLocationClient)
        }
        composable(route = AppScreens.CameraPreview.name) {
            CameraPreview()
        }
        composable(route = AppScreens.CameraCapture.name) {
            CameraCaptureScreen()
        }
    }
}