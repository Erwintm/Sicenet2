package com.example.marsphotos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.WorkInfo
import com.example.marsphotos.model.CargaAcademica



@Composable
fun CargaAcademicaScreen(
    viewModel: CargaViewModel = viewModel(),
    navController: NavController
) {

    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.cargarMaterias()
    }

    when {
        state.isLoading -> {
            CircularProgressIndicator()
        }

        state.error != null -> {
            Text("Error: ${state.error}")
        }

        else -> {
            ListaMaterias(state.materias)
        }
    }
}

@Composable
fun ListaMaterias(materias: List<CargaAcademica>) {

    LazyColumn {

        items(materias) { materia ->

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

                Column(Modifier.padding(12.dp)) {

                    Text(materia.Materia, fontSize = 18.sp)
                    Text("Docente: ${materia.Docente}")
                    Text("Grupo: ${materia.Grupo}")
                    Text("Créditos: ${materia.CreditosMateria}")

                    if (materia.Lunes.isNotBlank())
                        Text("Lunes: ${materia.Lunes}")
                    if (materia.Martes.isNotBlank())
                        Text("Martes: ${materia.Martes}")
                    if (materia.Miercoles.isNotBlank())
                        Text("Miércoles: ${materia.Miercoles}")
                    if (materia.Jueves.isNotBlank())
                        Text("Jueves: ${materia.Jueves}")
                    if (materia.Viernes.isNotBlank())
                        Text("Viernes: ${materia.Viernes}")
                }
            }
        }
    }
}

