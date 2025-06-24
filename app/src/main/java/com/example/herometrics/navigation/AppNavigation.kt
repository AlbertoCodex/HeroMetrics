package com.example.herometrics.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.herometrics.data.DataFirebase
import com.example.herometrics.ui.screens.character.CharacterScreen
import com.example.herometrics.ui.screens.character.CharacterViewModel
import com.example.herometrics.ui.screens.busqueda.BusquedaScreen
import com.example.herometrics.ui.screens.busqueda.BusquedaViewModel
import com.example.herometrics.ui.screens.comparar.CompararScreen
import com.example.herometrics.ui.screens.comparar.CompararViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavigation(auth: FirebaseAuth, db: FirebaseFirestore) {
    val navController = rememberNavController()
    val dataFirebase = remember { DataFirebase(auth, db) }

    val sharedViewModel: SharedViewModel = viewModel()

    NavHost(navController = navController, startDestination = AppScreens.Busqueda.route) {
        composable(route = AppScreens.Busqueda.route) {
            val viewModel = remember { BusquedaViewModel(dataFirebase) }

            BusquedaScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            route = AppScreens.Character.route,
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("servidor") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val servidor = backStackEntry.arguments?.getString("servidor") ?: ""

            val viewModel = remember { CharacterViewModel(dataFirebase) }

            LaunchedEffect(nombre, servidor) {
                viewModel.fetchCharacterStats(servidor, nombre)
            }

            CharacterScreen(navController, viewModel, sharedViewModel)
        }

        composable(
            route = AppScreens.Comparar.route,
            arguments = listOf(
                navArgument("spec") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val spec = backStackEntry.arguments?.getString("spec") ?: ""

            val viewModel = remember { CompararViewModel() }

            LaunchedEffect(spec) {
                viewModel.fetchTopSpecFromBoss(spec)
            }
            CompararScreen(navController, sharedViewModel, viewModel)
        }

        /*
        composable(route = AppScreens.Splash.route) {
            SplashScreen(navController)
        }
        */
    }
}