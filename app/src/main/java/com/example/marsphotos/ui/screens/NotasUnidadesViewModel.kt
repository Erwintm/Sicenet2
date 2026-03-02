package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.MateriaUnidades
import kotlinx.coroutines.launch

data class NotasUiState(
    val isLoading: Boolean = false,
    val materias: List<MateriaUnidades> = emptyList(),
    val error: String? = null
)

class NotasUnidadesViewModel(
    private val repository: SNRepository
) : ViewModel() {

    var uiState by mutableStateOf(NotasUiState())
        private set

    fun cargarNotas() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                // El repositorio debería devolver la lista de la DB o Nube
                val lista = repository.fetchNotasUnidadesRemote()
                uiState = uiState.copy(materias = lista, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = "Error al cargar calificaciones")
            }
        }
    }
}