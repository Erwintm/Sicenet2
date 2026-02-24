package com.example.marsphotos.workers

import android.content.Context


import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marsphotos.data.SNDao
import com.example.marsphotos.model.CargaAcademica

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class StoreCargaWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val snDao: SNDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val jsonString = inputData.getString("KEY_CARGA_JSON") ?: return Result.failure()

        return try {

            val type = object : TypeToken<List<CargaAcademica>>() {}.type
            val materias: List<CargaAcademica> = Gson().fromJson(jsonString, type)


            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            materias.forEach { it.fechaSincronizacion = fechaActual }


            snDao.insertarCarga(materias)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}