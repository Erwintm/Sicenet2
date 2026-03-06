package com.example.marsphotos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.marsphotos.MarsPhotosApplication
import com.google.gson.Gson

class CalFinalesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository
        return try {
            val listaFinales = repository.fetchCalifFinalesRemote()
            val json = Gson().toJson(listaFinales)
            Result.success(workDataOf("KEY_FINALES_JSON" to json))
        } catch (e: Exception) {
            Result.failure()
        }
    }
}