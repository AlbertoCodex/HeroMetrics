package com.example.herometrics.ui.screens.comparar

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.herometrics.api.armory.CharacterStats
import com.example.herometrics.api.armory.CharacterViewData
import com.example.herometrics.navigation.SharedViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer

@Composable
fun CompararScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    viewModel: CompararViewModel
) {
    val charStats = sharedViewModel.selectedCharacter.value
    val selectedCharacters by viewModel.selectedCharacters.collectAsState()
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val averageStats by viewModel.averageStats.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            error != null -> {
                Text(
                    text = "❗ Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            averageStats != null && charStats != null -> {
                val selectedCharacters by viewModel.selectedCharacters.collectAsState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 📌 Tarjeta principal del personaje seleccionado
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("👤 ${charStats.name}", style = MaterialTheme.typography.bodyLarge)
                        Text("🎭 ${charStats.spec}", style = MaterialTheme.typography.bodyLarge)
                        Text("📊 ${charStats.itemLevel}", style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Comparativa de estadísticas",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 📌 Gráfico de comparación
                    CharStatComparisonChart(
                        charStats = charStats,
                        averageStats = averageStats,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (selectedCharacters.isNotEmpty()) {
                        Text(
                            text = "Personajes comparados:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                        ) {
                            selectedCharacters.forEach { character ->
                                Text(
                                    text = "🔸 ${character.nombre} - ${character.servidor}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                // 📌 Fondo con mensaje cuando no hay personaje seleccionado
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se ha seleccionado ningún personaje.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Busca o selecciona un personaje para ver su comparación.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CharStatComparisonChart(
    charStats: CharacterViewData,
    averageStats: CharacterStats?,
    modifier: Modifier = Modifier
) {
    val customColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
    )

    val columnProvider = ColumnCartesianLayer.ColumnProvider.series(
        customColors.map { color ->
            rememberLineComponent(
                fill = fill(color),
                thickness = 24.dp // Ancho de las columnas
            )
        }
    )
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
            rememberColumnCartesianLayer(
                columnProvider = columnProvider
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = bottomAxis,
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}