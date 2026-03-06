package com.example.marsphotos.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.model.CargaAcademica
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AlmacenarCargaWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {

        val json = inputData.getString("KEY_CARGA_JSON") ?: return Result.failure()
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository

        return try {
            val listType = object : TypeToken<List<CargaAcademica>>() {}.type
            val materias: List<CargaAcademica> = Gson().fromJson(json, listType)

            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            materias.forEach { it.fechaSincronizacion = fechaActual }

            repository.insertLocalCarga(materias)

            Log.d("WORKER", "Datos guardados en BD local: $fechaActual")
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}