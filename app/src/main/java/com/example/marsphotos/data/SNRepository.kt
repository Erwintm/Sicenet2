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

            android.util.Log.d("SICENET_PERFIL", "XML RECIBIDO: $xml")

            // 1. Extraemos el contenido JSON que está dentro de la etiqueta Result
            val jsonContent = Regex("""<getAlumnoAcademicoWithLineamientoResult>([^<]+)""").find(xml)?.groupValues?.get(1)

            if (jsonContent != null) {
                // 2. Extraemos los campos específicos del JSON usando Regex
                val nombre = Regex("""\"nombre\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "No encontrado"
                val carrera = Regex("""\"carrera\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "No encontrada"

                // Nota: En este JSON no veo un campo "promedio", pero veo "cdtosAcumulados" y "semActual"
                // Por ahora pondré la especialidad o créditos, o puedes revisar si hay otro método para promedio
                val infoExtra = Regex("""\"especialidad\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: ""

                ProfileStudent(
                    matricula = m,
                    nombre = nombre,
                    carrera = carrera,
                    promedio = infoExtra // O el dato que prefieras mostrar en ese campo
                )
            } else {
                ProfileStudent(m, "Error", "Formato inesperado", "0.0")
            }

        } catch (e: Exception) {
            android.util.Log.e("SICENET_PERFIL", "ERROR: ${e.message}")
            ProfileStudent(m, "Error de Conexión", "", "0.0")
        }
    }
}