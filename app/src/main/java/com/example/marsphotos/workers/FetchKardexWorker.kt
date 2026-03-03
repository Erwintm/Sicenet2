package com.example.marsphotos.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marsphotos.MarsPhotosApplication
import androidx.work.workDataOf
import com.google.gson.Gson

class FetchKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository
        return try {
            val listaKardex = repository.fetchKardexRemote() // Obtenemos la lista
            if (listaKardex.isNotEmpty()) {
                // PASAR EL JSON AL SIGUIENTE (Como en Carga)
                val json = Gson().toJson(listaKardex)
                Result.success(workDataOf("KEY_KARDEX_JSON" to json))
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}