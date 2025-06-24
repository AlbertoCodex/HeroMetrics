package com.example.herometrics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.herometrics.data.DataFirebase
import com.example.herometrics.navigation.AppNavigation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.herometrics.ui.theme.HeroMetricsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Instancia de Firebase
        val firebase = DataFirebase(Firebase.auth, Firebase.firestore)
        super.onCreate(savedInstanceState)
        setContent {
            HeroMetricsTheme {
                AppNavigation(firebase.auth, firebase.db)
            }
        }
    }
}