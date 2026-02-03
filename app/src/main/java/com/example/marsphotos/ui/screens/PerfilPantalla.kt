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
    // Al entrar a la pantalla, pedimos los datos al servidor (Punto 6)
    LaunchedEffect(Unit) {
        viewModel.obtenerDatosPerfil(matricula)
    }

    val perfil = viewModel.perfilUiState

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Perfil Académico", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        if (perfil == null) {
            CircularProgressIndicator()
            Text("Cargando datos de SICENET...")
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nombre: ${perfil.nombre}")
                    Text("Matrícula: ${perfil.matricula}")
                    Text("Carrera: ${perfil.carrera}")
                    Text("Especialidad: ${perfil.promedio}")
                }
            }
        }
    }
}