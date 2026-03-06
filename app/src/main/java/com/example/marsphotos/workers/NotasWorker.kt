package com.example.marsphotos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.marsphotos.MarsPhotosApplication
import com.google.gson.Gson

class NotasWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository
        return try {
            val notas = repository.fetchNotasUnidadesRemote()
            val json = Gson().toJson(notas)
            Result.success(workDataOf("KEY_NOTAS_JSON" to json))
        } catch (e: Exception) {
            Result.failure()
        }
    }
}