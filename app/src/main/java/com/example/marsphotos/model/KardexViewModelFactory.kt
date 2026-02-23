package com.example.marsphotos.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.ui.screens.KardexViewModel

class KardexViewModelFactory(
    private val repository: SNRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KardexViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KardexViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}