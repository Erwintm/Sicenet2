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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import com.example.marsphotos.model.CargaAcademica



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaAcademicaScreen(
    viewModel: CargaViewModel = viewModel(factory = CargaViewModel.Factory)
) {
    // Observamos la lista de materias desde Room
    val materias by viewModel.uiState.collectAsState()

    // Observamos el estado del WorkManager (RUNNING, ENQUEUED, SUCCEEDED)
    val infoWorker by viewModel.syncStatus.collectAsState(initial = null)


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Carga Académica") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {


            if (infoWorker?.state == WorkInfo.State.RUNNING) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }


            if (materias.isNotEmpty()) {
                Text(
                    text = "Sincronizado el: ${materias.first().fechaSincronizacion}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }


            if (materias.isEmpty() && infoWorker?.state != WorkInfo.State.RUNNING) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay datos guardados. Conéctate a internet.")
                }
            } else {
                LazyColumn {
                    items(materias) { materia ->
                        CardMateria(materia)
                    }
                }
            }
        }
    }
}
@Composable
fun CardMateria(materia: CargaAcademica) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = materia.Materia, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "Docente: ${materia.Docente}", style = MaterialTheme.typography.bodyMedium)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Grupo: ${materia.Grupo}")

            }
            //  horarios (Lunes, Martes, etc.)
        }
    }
}