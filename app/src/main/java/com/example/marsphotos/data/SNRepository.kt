package com.example.marsphotos.data

import android.util.Log
import com.example.marsphotos.model.CargaAcademica
import com.example.marsphotos.model.Kardex // Importante importar tu nuevo modelo
import com.example.marsphotos.model.ProfileStudent
import com.example.marsphotos.network.SICENETWService
import com.example.marsphotos.network.getCargaXml
import com.example.marsphotos.network.getKardexXml
import com.example.marsphotos.network.getLoginXml
import com.example.marsphotos.network.getPerfilXml
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.marsphotos.model.KardexResponse
import com.example.marsphotos.model.KardexRaw


interface SNRepository {
    suspend fun acceso(m: String, p: String): String
    suspend fun profile(m: String): ProfileStudent
    suspend fun fetchCargaAcademica(): List<CargaAcademica>
    fun getCargaLocal(): Flow<List<CargaAcademica>>

    // --- EL CAMBIO ESTÁ AQUÍ ---
    suspend fun fetchKardexRemote(): List<Kardex> // Cambiado de String a List<Kardex>
    fun obtenerKardexLocal(): Flow<List<Kardex>>
}

class NetworkSNRepository(
    private val snApiService: SICENETWService,
    private val snDao: SNDao
) : SNRepository {

    override suspend fun acceso(m: String, p: String): String {
        return try {
            val xmlString = getLoginXml(m, p)
            val requestBody = xmlString.toRequestBody("text/xml".toMediaTypeOrNull())
            val res = snApiService.acceso(requestBody)
            val responseString = res.string()

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

    private fun parsearKardex(jsonString: String): List<Kardex> {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val fechaActual = sdf.format(Date())

            // 1. Convertimos el String JSON a los objetos KardexRaw que creaste
            val type = object : TypeToken<KardexResponse>() {}.type
            val response: KardexResponse = Gson().fromJson(jsonString, type)

            // 2. "Mapeamos" de KardexRaw (servidor) a Kardex (tu base de datos)
            response.lstKardex.map { raw ->
                Kardex(
                    clvMateria = raw.ClvMat ?: "",
                    materia = raw.Materia ?: "",
                    calificacion = raw.Calif ?: 0,
                    acreditacion = raw.Acred ?: "",
                    periodo = "${raw.P1 ?: ""} ${raw.A1 ?: ""}".trim(),
                    fechaSincronizacion = fechaActual
                )
            }
        } catch (e: Exception) {
            Log.e("KARDEX_PARSER", "Error parseando JSON: ${e.message}")
            emptyList() // Si algo falla, regresamos lista vacía para que no truene la app
        }
    }

    // --- IMPLEMENTACIÓN KARDEX ---

    // Cambia el retorno de String a List<Kardex>
    override suspend fun fetchKardexRemote(): List<Kardex> {
        return try {
            val xmlString = getKardexXml()
            val requestBody = xmlString.toRequestBody("text/xml; charset=utf-8".toMediaType())
            val response = snApiService.getKardex(requestBody)
            val xmlCompleto = response.string()

            // Sacamos el JSON que viene dentro del XML
            val regex = Regex("""<getAllKardexConPromedioByAlumnoResult>([\s\S]*?)</getAllKardexConPromedioByAlumnoResult>""", RegexOption.IGNORE_CASE)
            val contenidoJson = regex.find(xmlCompleto)?.groupValues?.get(1) ?: ""

            if (contenidoJson.isNotEmpty()) {
                val lista = parsearKardex(contenidoJson)
                if (lista.isNotEmpty()) {
                    // GUARDAR EN LA DB (SNDao)
                    snDao.insertarKardex(lista)
                    Log.d("KARDEX_DEBUG", "¡POR FIN! ${lista.size} materias guardadas en Room.")
                    return lista
                }
            }
            emptyList()
        } catch (e: Exception) {
            Log.e("KARDEX_DEBUG", "Error: ${e.message}")
            emptyList()
        }
    }

    override fun obtenerKardexLocal(): Flow<List<Kardex>> = snDao.obtenerKardex()

    // --- EL RESTO DE TUS FUNCIONES (Profile, Carga, etc) ---

    override suspend fun profile(m: String): ProfileStudent {
        // ... (Tu código de profile se mantiene igual)
        return try {
            val xmlString = getPerfilXml(m)
            val requestBody = xmlString.toRequestBody("text/xml; charset=utf-8".toMediaTypeOrNull())
            val response = snApiService.getPerfil(requestBody)
            val xml = response.string()
            val jsonContent = Regex("""<getAlumnoAcademicoWithLineamientoResult>([^<]+)""").find(xml)?.groupValues?.get(1)

            if (jsonContent != null) {
                ProfileStudent(
                    matricula = m,
                    nombre = Regex("""\"nombre\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Estudiante",
                    carrera = Regex("""\"carrera\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Carrera",
                    promedio = Regex("""\"especialidad\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Sin Especialidad",
                    semestre = Regex("""\"semActual\":(\d+)""").find(jsonContent)?.groupValues?.get(1) ?: "0",
                    creditos = Regex("""\"cdtosAcumulados\":(\d+)""").find(jsonContent)?.groupValues?.get(1) ?: "0",
                    fechaReins = Regex("""\"fechaReins\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "No disponible"
                )
            } else {
                ProfileStudent(m, "Error", "Formato inválido", "", "", "", "")
            }
        } catch (e: Exception) {
            ProfileStudent(m, "Error de red", "", "", "", "", "")
        }
    }

    override suspend fun fetchCargaAcademica(): List<CargaAcademica> {
        return try {
            val xmlString = getCargaXml()
            val requestBody = xmlString.toRequestBody("text/xml; charset=utf-8".toMediaType())
            val response = snApiService.getCarga(requestBody)
            val json = response.body?.response?.result ?: return emptyList()
            val listaType = object : TypeToken<List<CargaAcademica>>() {}.type
            Gson().fromJson(json, listaType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getCargaLocal(): Flow<List<CargaAcademica>> = snDao.obtenerCarga()
}

