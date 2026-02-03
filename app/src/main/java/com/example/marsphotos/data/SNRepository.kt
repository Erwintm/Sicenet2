package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.model.ProfileStudent
import com.example.marsphotos.model.Usuario
import com.example.marsphotos.network.SICENETWService
import com.example.marsphotos.network.bodyacceso
import com.example.marsphotos.network.bodyperfil
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception

interface SNRepository {
    suspend fun acceso(m: String, p: String): String
    suspend fun profile(m: String): ProfileStudent
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    override suspend fun acceso(m: String, p: String): String {
        return try {
            val res = snApiService.acceso(bodyacceso.format(m, p).toRequestBody())
            val responseString = res.string()

            // ESTO ES CLAVE: Mira en el Logcat qué está respondiendo el servidor realmente
            Log.d("SICENET_DEBUG", "Cuerpo recibido: $responseString")

            // Buscamos "true" ignorando si es mayúscula o minúscula
            if (responseString.contains("true", ignoreCase = true)) {
                "success"
            } else {
                // Si falla, regresemos el error que viene en el XML para saber qué pasó
                Log.e("SICENET_DEBUG", "Login fallido según el servidor")
                "invalid"
            }
        } catch (e: Exception) {
            Log.e("SICENET_REPOS", "Error de red: ${e.message}")
            "error"
        }
    }

    override suspend fun profile(m: String): ProfileStudent {
        return try {
            val res = snApiService.getPerfil(bodyperfil.toRequestBody())
            val xml = res.string()
            Log.d("SICENET_REPOS", "Profile Response: $xml")

            // Extracción de datos usando Regex (Punto 6 y 7)
            // El servidor devuelve un JSON/XML escapado dentro del resultado
            val nombre = Regex("""<nombre>([^<]+)""").find(xml)?.groupValues?.get(1) ?: "No encontrado"
            val carrera = Regex("""<carrera>([^<]+)""").find(xml)?.groupValues?.get(1) ?: "N/A"
            val promedio = Regex("""<promedioGeneral>([^<]+)""").find(xml)?.groupValues?.get(1) ?: "0.0"

            ProfileStudent(
                matricula = m,
                nombre = nombre,
                carrera = carrera,
                promedio = promedio
            )
        } catch (e: Exception) {
            Log.e("SICENET_REPOS", "Error profile", e)
            ProfileStudent(matricula = m, nombre = "Error al cargar")
        }
    }
}