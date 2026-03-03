package com.example.marsphotos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.model.MateriaUnidades
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class StoreNotasWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val json = inputData.getString("KEY_NOTAS_JSON") ?: return Result.failure()
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository

        return try {
            val listType = object : TypeToken<List<MateriaUnidades>>() {}.type
            val lista: List<MateriaUnidades> = Gson().fromJson(json, listType)

            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            lista.forEach { it.fechaSincronizacion = fechaActual }

            repository.insertarNotasLocal(lista) // A crear en el Repo
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}