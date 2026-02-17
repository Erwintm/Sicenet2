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


    val syncStatus = wmRepository.outputWorkInfo


    init {
        sincronizar()
    }

    fun sincronizar() {
        wmRepository.cargaAcademica()
    }
}