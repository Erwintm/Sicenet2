package com.example.marsphotos.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marsphotos.data.SNRepository

class NotasUnidadesViewModelFactory(
    private val repository: SNRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Le preguntamos: ¿Quieres crear un NotasUnidadesViewModel?
        if (modelClass.isAssignableFrom(NotasUnidadesViewModel::class.java)) {
            // Si sí, lo creamos pasándole el repositorio
            return NotasUnidadesViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}