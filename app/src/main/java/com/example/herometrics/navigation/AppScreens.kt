package com.example.herometrics.navigation

sealed class AppScreens(val route:String) {
    object Busqueda:AppScreens("busqueda")
    object Comparison:AppScreens("comparison")
    object Splash:AppScreens("splash")
    object Profile:AppScreens("profile")
}