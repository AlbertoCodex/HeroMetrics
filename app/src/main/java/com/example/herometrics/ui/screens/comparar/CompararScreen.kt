package com.example.herometrics.ui.screens.comparar

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.herometrics.api.armory.CharacterStats
import com.example.herometrics.api.armory.CharacterViewData
import com.example.herometrics.navigation.AppScreens
import com.example.herometrics.navigation.SharedViewModel
import com.example.herometrics.ui.screens.character.CharStatChart
import com.example.herometrics.ui.screens.character.StatCard
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
fun CompararScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    viewModel: CompararViewModel
) {
    val charStats = sharedViewModel.selectedCharacter.value
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val averageStats by viewModel.averageStats.observeAsState()

    when {
        isLoading -> {
            Log.d("UI", "Cargando...")
            CircularProgressIndicator()
        }

        error != null -> {
            Text("Error: $error")
        }

        averageStats != null -> {
            if (charStats != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Comparando personaje: ${charStats.name} - ${charStats.spec} (${charStats.itemLevel} iLvl)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Comparativa de estadísticas",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CharStatComparisonChart(
                        charStats = charStats,
                        averageStats = averageStats,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se ha seleccionado ningún personaje o no hay datos de comparación.")
                }
            }
        }

        else -> {
            Text("Esperando datos...")
        }
    }

/*

 */
}

@Composable
fun CharStatComparisonChart(
    charStats: CharacterViewData,        // Puedes extraer esto de selectedCharacter.value.stats o similar
    averageStats: CharacterStats?,
    modifier: Modifier = Modifier
) {
    val labels = listOf("Crítico", "Maestría", "Celeridad", "Versatilidad")

    // Extraemos los valores numéricos
    val playerValues = listOf(
        charStats.crit,
        charStats.mastery,
        charStats.haste,
        charStats.versatility
    )

    val averageValues = listOf(
        averageStats!!.crit,
        averageStats.mastery,
        averageStats.haste,
        averageStats.versatility
    )

    // Normalizamos ambos sets para visual comparativa
    val total = (playerValues + averageValues).sum().coerceAtLeast(1.0)
    val playerPercentages = playerValues.map { it.toFloat() / total * 100 }
    val averagePercentages = averageValues.map { it.toFloat() / total * 100 }

    val modelProducer = remember { CartesianChartModelProducer() }

    val bottomAxis = HorizontalAxis.rememberBottom(
        valueFormatter = { _, value, _ ->
            val index = value.toInt()
            labels.getOrNull(index).orEmpty()
        },
        itemPlacer = HorizontalAxis.ItemPlacer.segmented()
    )

    LaunchedEffect(playerPercentages, averagePercentages) {
        modelProducer.runTransaction {
            columnSeries {
                series(playerPercentages)
                series(averagePercentages)
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = bottomAxis,
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}