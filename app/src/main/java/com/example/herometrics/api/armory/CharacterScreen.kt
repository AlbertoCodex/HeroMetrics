package com.example.herometrics.api.armory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

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

@Composable
private fun JetpackComposeBasicColumnChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Composable
fun JetpackComposeBasicColumnChart(modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/eji9zq.
            columnSeries { series(5, 6, 5, 2, 11, 8, 5, 2, 15, 11, 8, 13, 12, 10, 2, 7) }
        }
    }
    JetpackComposeBasicColumnChart(modelProducer, modifier)
}