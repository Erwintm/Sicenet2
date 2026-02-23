package com.example.marsphotos.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marsphotos.MarsPhotosApplication

class FetchKardexWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository
        return try {
            val json = repository.fetchKardexRemote()
            if (json.isNotEmpty()) {
                // Aquí podrías guardar un archivo temporal o pasarlo al siguiente worker
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}