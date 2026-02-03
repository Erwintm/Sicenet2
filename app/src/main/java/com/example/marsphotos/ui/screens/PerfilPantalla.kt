package com.example.marsphotos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun PerfilPantalla(
    matricula: String,
    viewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.obtenerDatosPerfil(matricula)
    }

    val perfil = viewModel.perfilUiState

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Perfil Académico",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (perfil == null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cargando datos de SICENET...")
            }
        } else {
            // --- CUADRO 1: INFORMACIÓN PERSONAL ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información Personal", style = MaterialTheme.typography.titleMedium)
                  Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Nombre: ${perfil.nombre}")
                    Text("Matrícula: ${perfil.matricula}")
                    Text("Carrera: ${perfil.carrera}")
                    Text("Especialidad: ${perfil.promedio}") // Sigue llamándose promedio internamente
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CUADRO 2: ESTATUS ACADÉMICO (EL NUEVO) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Situación Escolar", style = MaterialTheme.typography.titleMedium)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Semestre Actual: ${perfil.semestre}°")
                    Text("Créditos Acumulados: ${perfil.creditos}")

                    val fechaLimpia = perfil.fechaReins.replace("|", " a las ")
                    Text("Próxima Reinscripción: $fechaLimpia")
                }
            }
        }
    }
}