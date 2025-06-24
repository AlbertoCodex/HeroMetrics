package com.example.herometrics.ui.screens.busqueda

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.herometrics.R
import com.example.herometrics.navigation.AppScreens

@Composable
fun BusquedaScreen(
    navController: NavHostController,
    viewModel: BusquedaViewModel
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        viewModel.cargarBusquedasRecientes()
        BusquedaContent(
            modifier = Modifier.align(Alignment.Center),
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun BusquedaContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: BusquedaViewModel
) {
    val busqueda by viewModel.busqueda.observeAsState(initial = "")
    val recientes by viewModel.recientes.observeAsState(emptyList())


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo centrado arriba
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(300.dp) // Puedes ajustar el tamaño a tu gusto
                .padding(bottom = 16.dp)
        )

        TextField(
            value = busqueda,
            onValueChange = { viewModel.setBusqueda(it) },
            label = { Text("Nombre-Servidor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val parts = busqueda.split("-")
                    if (parts.size == 2) {
                        val nombre = parts[0].trim()
                        val servidor = parts[1].trim()
                        navController.navigate(AppScreens.Character.createRoute(nombre.toLowerCase(), servidor.toLowerCase()))
                    } else {
                        // Aquí podrías mostrar un diálogo de error si el formato está mal
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = busqueda.contains("-")
            ) {
                Text("Buscar")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp) // Más separación arriba
        ) {
            Text(
                text = "Búsquedas recientes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn {
                items(recientes) { busqueda ->
                    Text(
                        text = "${busqueda.nombre}-${busqueda.servidor}",
                        style = MaterialTheme.typography.bodyLarge, // Mejor legibilidad
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setBusqueda(
                                    "${busqueda.nombre.capitalize()}-${busqueda.servidor.capitalize()}"
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp) // Buen espacio táctil
                    )
                    Divider() // Línea separadora entre elementos
                }
            }
        }
    }
}
