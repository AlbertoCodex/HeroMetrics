package com.example.herometrics.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.herometrics.data.DataFirebase
import com.example.herometrics.ui.screens.busqueda.BusquedaScreen
import com.example.herometrics.ui.screens.busqueda.BusquedaViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavigation(auth: FirebaseAuth, db: FirebaseFirestore) {
    val navController = rememberNavController()
    val dataFirebase = remember { DataFirebase(auth, db) }

    NavHost(navController = navController, startDestination = AppScreens.Busqueda.route) {
        composable(route = AppScreens.Busqueda.route) {
            val viewModel = remember { BusquedaViewModel(dataFirebase) }

            BusquedaScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        /*
        composable(route = AppScreens.Comparison.route) {
            ComparisonScreen()
        }

        composable(route = AppScreens.Splash.route) {
            SplashScreen(navController)
        }

        composable(route = AppScreens.Profile.route) {
            ProfileScreen(navController, ProfileViewModel(), db, auth)
        }
        */
    }
}