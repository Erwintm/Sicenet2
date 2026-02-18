package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.data.SNWMRepository
import com.example.marsphotos.model.CargaAcademica
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CargaUiState(
    val isLoading: Boolean = false,
    val materias: List<CargaAcademica> = emptyList(),
    val error: String? = null
)
class CargaViewModel(
    private val repository: SNRepository
) : ViewModel() {

    var uiState by mutableStateOf(CargaUiState())
        private set

    fun cargarMaterias() {
        viewModelScope.launch {

            uiState = uiState.copy(isLoading = true, error = null)

            try {
                val lista = repository.fetchCargaAcademica()

                uiState = uiState.copy(
                    isLoading = false,
                    materias = lista
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "No se pudo obtener la carga académica"
                )
            }
        }
    }
}
