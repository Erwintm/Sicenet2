package com.example.marsphotos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.marsphotos.MarsPhotosApplication
import com.google.gson.Gson

class FetchFinalesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository
        return try {
            // 1. Consultar finales al repositorio
            val listaFinales = repository.fetchCalifFinalesRemote()

            // 2. Convertir a JSON para el siguiente paso
            val json = Gson().toJson(listaFinales)

            // 3. Éxito: enviamos el JSON con la llave KEY_FINALES_JSON
            Result.success(workDataOf("KEY_FINALES_JSON" to json))
        } catch (e: Exception) {
            Result.failure()
        }
    }
}