package com.example.marsphotos.workers


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.marsphotos.data.SNRepository

class FetchKardexWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val snRepository: SNRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            // Nota: El repo aún no tiene esta función, la haremos en el paso 4
            val jsonResult = snRepository.fetchKardexRemote()
            if (jsonResult.isNotEmpty()) {
                Result.success(workDataOf("KEY_KARDEX_JSON" to jsonResult))
            } else Result.failure()
        } catch (e: Exception) { Result.retry() }
    }
}