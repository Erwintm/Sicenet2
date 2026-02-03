package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.model.ProfileStudent
import com.example.marsphotos.network.SICENETWService
import com.example.marsphotos.network.getLoginXml
import com.example.marsphotos.network.getPerfilXml
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

// 1. Definición de la Interfaz
interface SNRepository {
    suspend fun acceso(m: String, p: String): String
    suspend fun profile(m: String): ProfileStudent
}

// 2. Implementación de la Clase (Donde ocurre la magia)
class NetworkSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    override suspend fun acceso(m: String, p: String): String {
        return try {
            // Generamos el XML usando la interpolación de $
            val xmlString = getLoginXml(m, p)
            val requestBody = xmlString.toRequestBody("text/xml".toMediaTypeOrNull())

            val res = snApiService.acceso(requestBody)
            val responseString = res.string()

            Log.d("SICENET_DEBUG", "Login Respuesta: $responseString")

            // Verificamos el JSON de éxito que vimos en tu Logcat
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

            // Extraemos el bloque JSON del XML
            val jsonContent = Regex("""<getAlumnoAcademicoWithLineamientoResult>([^<]+)""").find(xml)?.groupValues?.get(1)

            if (jsonContent != null) {
                // 1. Extraemos los datos básicos
                val nombre = Regex("""\"nombre\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Estudiante"
                val carrera = Regex("""\"carrera\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Carrera"

                // 2. Especialidad (mapeada a 'promedio' para no cambiar el data class)
                val especialidad = Regex("""\"especialidad\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Sin Especialidad"

                // 3. Los nuevos campos para el segundo cuadro
                val semestre = Regex("""\"semActual\":(\d+)""").find(jsonContent)?.groupValues?.get(1) ?: "0"
                val creditos = Regex("""\"cdtosAcumulados\":(\d+)""").find(jsonContent)?.groupValues?.get(1) ?: "0"
                val fecha = Regex("""\"fechaReins\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "No disponible"

                ProfileStudent(
                    matricula = m,
                    nombre = nombre,
                    carrera = carrera,
                    promedio = especialidad, // Mantenemos el nombre del campo como pediste
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