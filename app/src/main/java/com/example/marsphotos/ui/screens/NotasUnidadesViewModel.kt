package com.example.marsphotos.ui.screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.marsphotos.data.SNRepository
import com.example.marsphotos.model.MateriaUnidades
import com.example.marsphotos.workers.FetchNotasWorker
import com.example.marsphotos.workers.StoreNotasWorker
import kotlinx.coroutines.launch

data class NotasUiState(
    val isLoading: Boolean = false,
    val materias: List<MateriaUnidades> = emptyList(),
    val error: String? = null
)

class NotasUnidadesViewModel(
    application: Application,
    private val repository: SNRepository
) : AndroidViewModel(application) {

    var uiState by mutableStateOf(NotasUiState())
        private set

    private val workManager = WorkManager.getInstance(application)

    // Monitoreo del status para la UI (Punto 2b)
    val syncWorkInfo = workManager.getWorkInfosForUniqueWorkLiveData("sync_notas")

    init {
        // LEER SIEMPRE DE LOCAL (Punto 1 y Requisito Offline)
        viewModelScope.launch {
            repository.obtenerNotasLocal().collect { lista ->
                uiState = uiState.copy(materias = lista)
            }
        }
    }

    fun cargarNotas(isOnline: Boolean) {
        if (isOnline) {
            sincronizarConWorkers()
        }
    }

    private fun sincronizarConWorkers() {
        // ENCADENAMIENTO ÚNICO (Punto 2b)
        val fetch = OneTimeWorkRequestBuilder<FetchNotasWorker>().build()
        val store = OneTimeWorkRequestBuilder<StoreNotasWorker>().build()

        workManager.beginUniqueWork(
            "sync_notas",
            ExistingWorkPolicy.REPLACE,
            fetch
        ).then(store).enqueue()
    }
}