package com.example.marsphotos.ui.screens



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.Kardex
import com.google.gson.Gson
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
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

            // PASO 1: Intentar traer de la base de datos local primero (Offline-First)
            repository.obtenerKardexLocal().collect { listaLocal ->
                uiState = uiState.copy(materias = listaLocal)

                // PASO 2: Si está vacío o queremos actualizar, intentamos ir a la nube
                if (listaLocal.isEmpty()) {
                    try {
                        val json = repository.fetchKardexRemote()
                        if (json.isNotEmpty()) {
                            // Aquí el Worker normalmente se encargaría de guardar,
                            // pero para mostrarlo rápido podemos parsearlo:
                            val type = object : TypeToken<List<Kardex>>() {}.type
                            val listaNube: List<Kardex> = Gson().fromJson(json, type)

                            uiState = uiState.copy(
                                isLoading = false,
                                materias = listaNube
                            )
                        }
                    } catch (e: Exception) {
                        uiState = uiState.copy(
                            isLoading = false,
                            error = "Modo Offline: No se pudo actualizar desde SICENET"
                        )
                    }
                } else {
                    uiState = uiState.copy(isLoading = false)
                }
            }
        }
    }

    // El Factory se suele poner aquí abajo o en un archivo aparte
    companion object {
        // Usaremos el Factory que crearemos a continuación
    }
}