package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY // IMPORTANTE
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.ProfileStudent
import kotlinx.coroutines.launch

class PerfilViewModel(private val snRepository: SNRepository) : ViewModel() {

    var perfilUiState by mutableStateOf<ProfileStudent?>(null)
    var isRefreshing by mutableStateOf(false)

    fun obtenerDatosPerfil(matricula: String) {
        viewModelScope.launch {
            isRefreshing = true
            perfilUiState = snRepository.profile(matricula)
            isRefreshing = false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {

                val application = (this[APPLICATION_KEY] as MarsPhotosApplication)
                val repository = application.container.snRepository
                PerfilViewModel(snRepository = repository)
            }
        }
    }
}