package com.example.herometrics.screens.busqueda

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

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
    val showDialog by viewModel.showDialog.observeAsState(initial = false)
    val errorDialog by viewModel.errorDialog.observeAsState(initial = "")


    Column(modifier = modifier.padding(16.dp)) {
        TextField(
            value = busqueda,
            onValueChange = { viewModel.setBusqueda(it) },
            label = { Text("Nombre - Servidor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    // navController.navigate(comparar")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Comparar")
            }

            Button(
                onClick = {
                    navController.navigate("profile")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Buscar")
            }
        }

        Text("Búsquedas recientes", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(recientes) { busqueda ->
                Text(
                    text = busqueda.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { viewModel.setBusqueda(busqueda.nombre) }
                )
            }
        }
    }
}
