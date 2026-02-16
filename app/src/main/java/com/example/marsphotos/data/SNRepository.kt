package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.model.CargaAcademica
import com.example.marsphotos.model.ProfileStudent
import com.example.marsphotos.network.SICENETWService
import com.example.marsphotos.network.getLoginXml
import com.example.marsphotos.network.getPerfilXml
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


interface SNRepository {
    suspend fun acceso(m: String, p: String): String
    suspend fun profile(m: String): ProfileStudent

}


class NetworkSNRepository(
    private val snApiService: SICENETWService,

) : SNRepository {

    override suspend fun acceso(m: String, p: String): String {
        return try {

            val xmlString = getLoginXml(m, p)
            val requestBody = xmlString.toRequestBody("text/xml".toMediaTypeOrNull())

            val res = snApiService.acceso(requestBody)
            val responseString = res.string()

            Log.d("SICENET_DEBUG", "Login Respuesta: $responseString")


            if (responseString.contains("\"acceso\":true", ignoreCase = true)) {
                "success"
            } else {
                "invalid"
            }
        } catch (e: Exception) {
            Log.e("SICENET_DEBUG", "Error Login: ${e.message}")
            "error"
        }
    }

    override suspend fun profile(m: String): ProfileStudent {
        return try {
            val xmlString = getPerfilXml(m)
            val requestBody = xmlString.toRequestBody("text/xml; charset=utf-8".toMediaTypeOrNull())

            val response = snApiService.getPerfil(requestBody)
            val xml = response.string()


            val jsonContent = Regex("""<getAlumnoAcademicoWithLineamientoResult>([^<]+)""").find(xml)?.groupValues?.get(1)

            if (jsonContent != null) {

                val nombre = Regex("""\"nombre\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Estudiante"
                val carrera = Regex("""\"carrera\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Carrera"


                val especialidad = Regex("""\"especialidad\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Sin Especialidad"


                val semestre = Regex("""\"semActual\":(\d+)""").find(jsonContent)?.groupValues?.get(1) ?: "0"
                val creditos = Regex("""\"cdtosAcumulados\":(\d+)""").find(jsonContent)?.groupValues?.get(1) ?: "0"
                val fecha = Regex("""\"fechaReins\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "No disponible"

                ProfileStudent(
                    matricula = m,
                    nombre = nombre,
                    carrera = carrera,
                    promedio = especialidad,
                    semestre = semestre,
                    creditos = creditos,
                    fechaReins = fecha
                )
            } else {
                ProfileStudent(m, "Error", "Formato inválido", "", "", "", "")
            }

        } catch (e: Exception) {
            android.util.Log.e("SICENET_PERFIL", "Error: ${e.message}")
            ProfileStudent(m, "Error de red", "", "", "", "", "")
        }
    }

}