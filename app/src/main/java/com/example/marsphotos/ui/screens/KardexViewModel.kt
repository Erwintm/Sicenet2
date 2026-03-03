package com.example.marsphotos.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.Kardex

import com.example.marsphotos.workers.StoreKardexWorker
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import com.example.marsphotos.data.FetchKardexWorker

data class KardexUiState(
    val isLoading: Boolean = false,
    val materias: List<Kardex> = emptyList(),
    val error: String? = null
)

// Cambiamos a AndroidViewModel para tener acceso al Context para WorkManager
class KardexViewModel(
    application: Application,
    private val repository: SNRepository
) : AndroidViewModel(application) {

    var uiState by mutableStateOf(KardexUiState())
        private set

    private val workManager = WorkManager.getInstance(application)

    // Observamos el estatus de la sincronización (Punto 2b: Monitorear estatus)
    val syncWorkInfo = workManager.getWorkInfosForUniqueWorkLiveData("sync_kardex")

    init {
        // 1. Siempre observar la base de datos local (Single Source of Truth)
        viewModelScope.launch {
            repository.obtenerKardexLocal().collect { lista ->
                uiState = uiState.copy(materias = lista)
            }
        }
    }

    fun cargarKardex(isOnline: Boolean) {
        if (isOnline) {
            sincronizarConWorkers()
        }
    }

    private fun sincronizarConWorkers() {
        // Punto 2b: Dos peticiones de trabajo que se ven como únicos
        val fetchKardex = OneTimeWorkRequestBuilder<FetchKardexWorker>().build()
        val storeKardex = OneTimeWorkRequestBuilder<StoreKardexWorker>().build()

        workManager.beginUniqueWork(
            "sync_kardex",
            ExistingWorkPolicy.REPLACE,
            fetchKardex
        ).then(storeKardex).enqueue()
    }
}