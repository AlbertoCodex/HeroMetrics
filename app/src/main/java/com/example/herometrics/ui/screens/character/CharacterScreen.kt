package com.example.herometrics.ui.screens.character

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.herometrics.navigation.AppScreens
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
fun CharacterScreen(
    navController: NavHostController,
    viewModel: CharacterViewModel,
    sharedViewModel: SharedViewModel
) {
    val charStats = viewModel.characterViewData
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

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
                Text("Error: $error")
            }

            charStats != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {

                    // Imagen al inicio
                    if (charStats.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = charStats.imageUrl,
                            contentDescription = "Render del personaje",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.7f)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Fila con nombre, especialidad e item level
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium // Bordes con esquinas redondeadas suaves
                            )
                            .padding(12.dp), // Espaciado interno para que el contenido no pegue al borde
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("👤 ${charStats.name}", style = MaterialTheme.typography.bodyLarge)
                        Text("🎭 ${charStats.spec}", style = MaterialTheme.typography.bodyLarge)
                        Text("📊 ${charStats.itemLevel}", style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estadísticas en 4 bloques organizados en 2 filas
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard("🎯 Crítico", charStats.crit.toInt())
                            StatCard("⚡ Celeridad", charStats.haste.toInt())
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard("💎 Maestría", charStats.mastery.toInt())
                            StatCard("🔄 Versatilidad", charStats.versatility.toInt())
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón al final
                    Button(
                        onClick = {
                            sharedViewModel.setSelectedCharacter(viewModel.characterViewData!!)
                            navController.navigate(AppScreens.Comparar.createRoute(charStats.spec))
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Comparar")
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Distribución porcentual de estadísticas",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CharStatChart(
                        crit = charStats.crit.toInt(),
                        haste = charStats.haste.toInt(),
                        mastery = charStats.mastery.toInt(),
                        versatility = charStats.versatility.toInt(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: Int) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value.toString(), style = MaterialTheme.typography.titleMedium)
        }
    }
}



@Composable
fun CharStatChart(
    crit: Int,
    mastery: Int,
    haste: Int,
    versatility: Int,
    modifier: Modifier = Modifier,
) {
    val customColors = listOf(
        MaterialTheme.colorScheme.primary,
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
    val rawValues = listOf(crit, mastery, haste, versatility)

    val total = rawValues.sum().coerceAtLeast(1) // evita división por 0
    val percentages = rawValues.map { it.toFloat() / total * 100 }

    val modelProducer = remember { CartesianChartModelProducer() }

    val bottomAxis = HorizontalAxis.rememberBottom(
        valueFormatter = { _, value, _ ->
            val index = value.toInt()
            labels.getOrNull(index).orEmpty()
        },
        itemPlacer = HorizontalAxis.ItemPlacer.segmented()
    )

    LaunchedEffect(percentages) {
        modelProducer.runTransaction {
            columnSeries {
                series(percentages)
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