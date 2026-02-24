package com.example.marsphotos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.model.CargaAcademica
import com.example.marsphotos.model.CargaViewModel
import com.example.marsphotos.model.CargaViewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

@Composable
fun CargaAcademicaScreen(viewModel: CargaViewModel,navController: NavController) {
    val listaCarga by viewModel.materias.collectAsState()
    val cargando by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2))) {
        // Encabezado Verde
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1B5E20)
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Text("MATERIA / DOCENTE", color = Color.White, modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelLarge)
                Text("HORARIO", color = Color.White, modifier = Modifier.weight(2f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge)
            }
        }

        if (cargando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFFE65100))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listaCarga) { materia ->
                CargaItemRow(materia)
            }
        }
    }
}

@Composable
fun CargaItemRow(carga: CargaAcademica) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).height(IntrinsicSize.Min)) {
            // Columna Materia
            Column(modifier = Modifier.weight(2f)) {
                Text(carga.Materia, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp)
                Text(carga.Docente, color = Color(0xFFE65100), fontSize = 11.sp, lineHeight = 14.sp)
            }

            // Columna Horarios
            Column(modifier = Modifier.weight(2f), horizontalAlignment = Alignment.End) {
                val dias = listOf("L" to carga.Lunes, "M" to carga.Martes, "Mi" to carga.Miercoles, "J" to carga.Jueves, "V" to carga.Viernes)
                dias.forEach { (label, horario) ->
                    if (horario.isNotBlank()) {
                        Text("$label: ${horario.replace(" Aula:", " | ")}", fontSize = 10.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderTabla() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20)) // Verde oscuro
            .padding(12.dp)
    ) {
        Text("MATERIA / DOCENTE", color = Color.White, modifier = Modifier.weight(2f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("GRUPO", color = Color.White, modifier = Modifier.weight(0.5f), fontSize = 12.sp, textAlign = TextAlign.Center)
        Text("HORARIO", color = Color.White, modifier = Modifier.weight(2f), fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

