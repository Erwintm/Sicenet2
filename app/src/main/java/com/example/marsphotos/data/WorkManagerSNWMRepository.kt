package com.example.marsphotos.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.marsphotos.workers.FetchCargaWorker
import com.example.marsphotos.workers.LoginDBWorker
import com.example.marsphotos.workers.LoginWorker
import com.example.marsphotos.workers.StoreCargaWorker
import kotlinx.coroutines.flow.Flow

class WorkManagerSNWMRepository(ctx: Context): SNWMRepository {

    private val workManager = WorkManager.getInstance(ctx)
    override val outputWorkInfo: Flow<WorkInfo>
        get() = TODO("Not yet implemented")

    override fun login(m: String, p: String) {
        // Add WorkRequest to Cleanup temporary images
        var continuation = workManager
            .beginUniqueWork(
                "SICENET_MANIPULATION_WORK_NAME",
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<LoginWorker>()
                    .addTag("EsteQuieroMonitorear").build()
            )


        val wrdbWorker = OneTimeWorkRequestBuilder<LoginDBWorker>()

        continuation = continuation.then(wrdbWorker.build())


        continuation.enqueue()

    }

    override fun profile() {
        //TODO("Not yet implemented")
    }

    override fun cargaAcademica() {

        val restricciones = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val fetchCarga = OneTimeWorkRequestBuilder<FetchCargaWorker>()
            .setConstraints(restricciones)
            .build()


        val storeCarga = OneTimeWorkRequestBuilder<StoreCargaWorker>().build()

        workManager
            .beginUniqueWork("carga_sync", ExistingWorkPolicy.KEEP, fetchCarga)
            .then(storeCarga)
            .enqueue()
    }
}