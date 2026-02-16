package com.example.marsphotos.ui.screens

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

class CargaViewModel(
    private val repository: SNRepository,
    private val wmRepository: SNWMRepository
) : ViewModel() {


    val uiState: StateFlow<List<CargaAcademica>> = repository.getCargaLocal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // 1. Obtenemos el contexto de la aplicación
                val application = (this[APPLICATION_KEY] as BaseApplication)

                // 2. Extraemos los repositorios del contenedor (AppContainer)
                // Asegúrate de que estos nombres coincidan con los de tu AppContainer
                val snRepository = application.container.snRepository
                val wmRepository = application.container.wmRepository

                CargaViewModel(
                    repository = snRepository,
                    wmRepository = wmRepository
                )
            }
        }
    }

    val syncStatus = wmRepository.outputWorkInfo


    init {
        sincronizar()
    }

    fun sincronizar() {
        wmRepository.cargaAcademica()
    }
}