package com.example.marsphotos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.marsphotos.data.SNRepository

class FetchCargaWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val snRepository: SNRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val jsonResult = snRepository.fetchCargaAcademica()

            if (jsonResult.isNotEmpty()) {

                val outputData = workDataOf("KEY_CARGA_JSON" to jsonResult)
                Result.success(outputData)
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}