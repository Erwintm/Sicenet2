package com.example.marsphotos.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.Kardex
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
                // 1. Intentamos leer de la DB local
                val listaLocal = repository.obtenerKardexLocal().first()
                Log.d("KARDEX_DEBUG", "Datos locales encontrados: ${listaLocal.size}")

                if (listaLocal.isNotEmpty()) {
                    uiState = uiState.copy(materias = listaLocal, isLoading = false)
                } else {
                    // 2. Si está vacío, vamos a la nube
                    Log.d("KARDEX_DEBUG", "DB vacía, consultando SICENET...")
                    val json = repository.fetchKardexRemote()

                    if (json.isNotEmpty()) {
                        val type = object : TypeToken<List<Kardex>>() {}.type
                        val listaNube: List<Kardex> = Gson().fromJson(json, type)

                        // OJO: Aquí deberías usar el Worker para guardar,
                        // pero para que VEAS algo ya, actualizamos el estado:
                        uiState = uiState.copy(materias = listaNube, isLoading = false)
                        Log.d("KARDEX_DEBUG", "Datos recibidos de nube: ${listaNube.size}")
                    } else {
                        uiState = uiState.copy(isLoading = false, error = "No se recibieron datos del servidor")
                    }
                }
            } catch (e: Exception) {
                Log.e("KARDEX_DEBUG", "Error: ${e.message}")
                uiState = uiState.copy(isLoading = false, error = "Error de conexión")
            }
        }
    }
}