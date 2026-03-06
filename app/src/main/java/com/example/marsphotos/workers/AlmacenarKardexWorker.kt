package com.example.marsphotos.workers



import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.model.Kardex
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class AlmacenarKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val json = inputData.getString("KEY_KARDEX_JSON") ?: return Result.failure()
        val repository = (applicationContext as MarsPhotosApplication).container.snRepository

        return try {
            val listType = object : TypeToken<List<Kardex>>() {}.type
            val materias: List<Kardex> = Gson().fromJson(json, listType)
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            materias.forEach { it.fechaSincronizacion = fechaActual }
            repository.insertarKardexLocal(materias)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}