package com.example.marsphotos.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.data.SNRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CargaViewModel(private val repository: SNRepository) : ViewModel() {


    private val _materias = MutableStateFlow<List<CargaAcademica>>(emptyList())
    val materias: StateFlow<List<CargaAcademica>> = _materias.asStateFlow()

    // Estado para la carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        obtenerCarga()
    }

    fun obtenerCarga() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("DEBUG_CARGA", "Iniciando petición...")
            try {
                val resultado = repository.fetchCargaAcademica()
                Log.d("DEBUG_CARGA", "Datos recibidos: ${resultado.size} materias")
                _materias.value = resultado
            } catch (e: Exception) {
                Log.e("DEBUG_CARGA", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }//N_c6$x
}