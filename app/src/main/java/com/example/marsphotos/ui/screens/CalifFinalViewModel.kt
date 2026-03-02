package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.CalifFinal
import kotlinx.coroutines.launch

data class FinalUiState(
    val isLoading: Boolean = false,
    val listaFinal: List<CalifFinal> = emptyList(),
    val error: String? = null
)

class CalifFinalViewModel(
    private val repository: SNRepository
) : ViewModel() {

    var uiState by mutableStateOf(FinalUiState())
        private set

    fun cargarFinales() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val lista = repository.fetchCalifFinalesRemote()
                uiState = uiState.copy(
                    listaFinal = lista,
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error al conectar con SICENET"
                )
            }
        }
    }
}