package com.example.marsphotos.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.Kardex
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class KardexUiState(
    val isLoading: Boolean = false,
    val materias: List<Kardex> = emptyList(),
    val error: String? = null
)

class KardexViewModel(
    private val repository: SNRepository
) : ViewModel() {

    var uiState by mutableStateOf(KardexUiState())
        private set

    fun cargarKardex() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            Log.d("KARDEX_DEBUG", "Iniciando carga...")

            try {
                // 1. Intentamos leer de la DB local primero
                val listaLocal = repository.obtenerKardexLocal().first()
                Log.d("KARDEX_DEBUG", "Datos locales encontrados: ${listaLocal.size}")

                if (listaLocal.isNotEmpty()) {
                    uiState = uiState.copy(materias = listaLocal, isLoading = false)
                } else {
                    // 2. Si está vacío, consultamos al Repositorio
                    Log.d("KARDEX_DEBUG", "DB vacía, consultando SICENET...")

                    // IMPORTANTE: El repositorio ya debe devolver List<Kardex>
                    // y no un String/JSON.
                    val listaNube = repository.fetchKardexRemote()

                    if (listaNube.isNotEmpty()) {
                        uiState = uiState.copy(materias = listaNube, isLoading = false)
                        Log.d("KARDEX_DEBUG", "¡Éxito! UI actualizada con ${listaNube.size} materias.")
                    } else {
                        uiState = uiState.copy(isLoading = false, error = "El Kardex está vacío en el servidor")
                    }
                }
            } catch (e: Exception) {
                Log.e("KARDEX_DEBUG", "Error en ViewModel: ${e.message}")
                uiState = uiState.copy(isLoading = false, error = "Error al procesar datos")
            }
        }
    }
}