package com.example.marsphotos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.model.CalifFinal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class AlmacenarFinalesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        // 1. Recibir el JSON del worker anterior
        val json = inputData.getString("KEY_FINALES_JSON") ?: return Result.failure()
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository

        return try {
            val listType = object : TypeToken<List<CalifFinal>>() {}.type
            val lista: List<CalifFinal> = Gson().fromJson(json, listType)

            // 2. Sello de fecha (Punto 2b del requerimiento)
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            lista.forEach { it.fechaSincronizacion = fechaActual }

            // 3. Guardar en Room a través del repositorio
            repository.insertarFinalesLocal(lista)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}