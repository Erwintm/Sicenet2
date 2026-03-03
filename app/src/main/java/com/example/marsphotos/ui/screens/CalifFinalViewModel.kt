package com.example.marsphotos.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.CalifFinal
import com.example.marsphotos.workers.FetchFinalesWorker
import com.example.marsphotos.workers.StoreFinalesWorker
import kotlinx.coroutines.launch

data class FinalUiState(
    val isLoading: Boolean = false,
    val listaFinal: List<CalifFinal> = emptyList(),
    val error: String? = null
)

class CalifFinalViewModel(
    application: Application,
    private val repository: SNRepository
) : AndroidViewModel(application) {

    var uiState by mutableStateOf(FinalUiState())
        private set

    private val workManager = WorkManager.getInstance(application)

    // Monitoreo del status para la UI (Requisito 2b)
    val syncWorkInfo = workManager.getWorkInfosForUniqueWorkLiveData("sync_finales")

    init {
        // Escuchamos la base de datos local siempre (Offline-First)
        viewModelScope.launch {
            repository.obtenerFinalesLocal().collect { lista ->
                uiState = uiState.copy(listaFinal = lista)
            }
        }
    }

    fun cargarFinales(isOnline: Boolean) {
        if (isOnline) {
            sincronizarConWorkers()
        }
    }

    private fun sincronizarConWorkers() {
        // Encadenamiento: Fetch -> Store
        val fetch = OneTimeWorkRequestBuilder<FetchFinalesWorker>().build()
        val store = OneTimeWorkRequestBuilder<StoreFinalesWorker>().build()

        workManager.beginUniqueWork(
            "sync_finales",
            ExistingWorkPolicy.REPLACE,
            fetch
        ).then(store).enqueue()
    }
}