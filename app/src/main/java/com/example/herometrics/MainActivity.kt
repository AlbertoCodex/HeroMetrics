package com.example.herometrics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.herometrics.test.CharacterScreen
import com.example.herometrics.test.JetpackComposeBasicColumnChart
import com.example.herometrics.ui.theme.HeroMetricsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroMetricsTheme {
                CharacterScreen()
                JetpackComposeBasicColumnChart()
            }
        }
    }
}