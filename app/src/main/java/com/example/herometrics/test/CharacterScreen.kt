package com.example.herometrics.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CharacterScreen(viewModel: CharacterViewModel = viewModel()) {
    val stats = viewModel.stats
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    when {
        isLoading -> {
            CircularProgressIndicator()
        }

        error != null -> {
            Text("Error: $error")
        }

        stats != null -> {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🛡️ Vida: ${stats.health}")
                Text("⚡ Poder: ${stats.power} (${stats.power_type.name})") // Muestra el nombre del tipo de poder
                Text("🔄 Versatilidad: ${stats.versatility}")
                Text("💪 Fuerza: ${stats.strength.base}")
                Text("🏃 Agilidad: ${stats.agility.base}")
                Text("🧠 Intelecto: ${stats.intellect.base}")
                Text("🎯 Crítico: ${stats.melee_crit.rating_bonus}")
                Text("💎 Maestría: ${stats.mastery.rating_bonus}")
            }
        }
    }
}