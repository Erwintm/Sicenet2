package com.example.marsphotos.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.ui.screens.CargaViewModel

class CargaViewModelFactory(
    private val repository: SNRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CargaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CargaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}