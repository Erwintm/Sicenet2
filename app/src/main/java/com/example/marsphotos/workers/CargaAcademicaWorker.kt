package com.example.marsphotos.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.marsphotos.MarsPhotosApplication
import com.google.gson.Gson

class CargaAcademicaWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository

        return try {

            val materias = repository.traerCargaAcademica()
            val jsonMaterias = Gson().toJson(materias)
            val outputData = workDataOf("KEY_CARGA_JSON" to jsonMaterias)
            Log.d("WORKER", "Carga exitoso, enviando datos al siguiente...")
            Result.success(outputData)
        } catch (e: Exception) {
            Log.e("WORKER", "Error consultando API", e)
            Result.failure()
        }
    }
}