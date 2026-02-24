package com.example.marsphotos.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasUnidadesScreen(viewModel: NotasUnidadesViewModel) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.cargarNotas()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Calificaciones por Unidad") })
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(uiState.materias) { materia ->
                    MateriaNotaCard(materia.materia, materia.unidades)
                }
            }
        }
    }
}

@Composable
fun MateriaNotaCard(nombre: String, unidades: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Intentamos separar por coma, espacio o pipe (|)
            val listaNotas = unidades.split(",").filter { it.isNotBlank() }
            

            if (listaNotas.isEmpty()) {
                Text("Sin calificaciones aún", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            } else {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listaNotas.forEachIndexed { index, nota ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text("U${index + 1}: $nota") },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        }
    }
}