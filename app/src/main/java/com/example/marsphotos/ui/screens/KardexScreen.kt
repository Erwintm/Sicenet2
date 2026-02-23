package com.example.marsphotos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.model.Kardex
import com.example.marsphotos.model.KardexViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(
    navController: androidx.navigation.NavController
) {
    val application = LocalContext.current.applicationContext as MarsPhotosApplication

    // Usamos el Factory para inyectar el repositorio
    val viewModel: KardexViewModel = viewModel(
        factory = KardexViewModelFactory(application.container.snRepository)
    )

    val state = viewModel.uiState

    // Disparamos la carga al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarKardex()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kardex Académico") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Requisito del profe: Mostrar fecha de última actualización si hay datos
            state.materias.firstOrNull()?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {

                }
            }

            if (state.isLoading && state.materias.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.materias) { item ->
                        KardexItem(item)
                    }
                }
            }

            if (state.error != null && state.materias.isEmpty()) {
                Text(text = state.error, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Composable
fun KardexItem(kardex: Kardex) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = kardex.materia,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${kardex.periodo} | ${kardex.acreditacion}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Coloreamos la calificación: Azul si pasó, Rojo si no
            Text(
                text = kardex.calificacion.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (kardex.calificacion >= 70) Color(0xFF1976D2) else Color.Red
            )
        }
    }
}