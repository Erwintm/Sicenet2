package com.example.marsphotos.data

import com.example.marsphotos.model.*
import com.example.marsphotos.network.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

interface SNRepository {
    // Auth & Perfil
    suspend fun acceso(m: String, p: String): String
    suspend fun profile(m: String): ProfileStudent

    // Carga Académica
    suspend fun traerCargaAcademica(): List<CargaAcademica>
    fun obtenerCarga(): Flow<List<CargaAcademica>>
    suspend fun insertLocalCarga(materias: List<CargaAcademica>)

    // Kardex
    suspend fun fetchKardexRemote(): List<Kardex>
    fun obtenerKardexLocal(): Flow<List<Kardex>>
    suspend fun insertarKardexLocal(lista: List<Kardex>)

    // Notas por Unidad
    suspend fun fetchNotasUnidadesRemote(): List<MateriaUnidades>
    fun obtenerNotasLocal(): Flow<List<MateriaUnidades>>
    suspend fun insertarNotasLocal(lista: List<MateriaUnidades>)

    // Calificaciones Finales
    suspend fun fetchCalifFinalesRemote(): List<CalifFinal>
    fun obtenerFinalesLocal(): Flow<List<CalifFinal>>
    suspend fun insertarFinalesLocal(lista: List<CalifFinal>)
}

class NetworkSNRepository(
    private val snApiService: SICENETWService,
    private val snDao: SNDao
) : SNRepository {

    //AUTENTICACIÓN
    override suspend fun acceso(m: String, p: String): String {
        return try {
            val xmlString = getLoginXml(m, p)
            val requestBody = xmlString.toRequestBody("text/xml".toMediaTypeOrNull())
            val res = snApiService.acceso(requestBody)
            val responseString = res.string()
            if (responseString.contains("\"acceso\":true", ignoreCase = true)) "success" else "invalid"
        } catch (e: Exception) { "error" }
    }

    //PERFIL
    override suspend fun profile(m: String): ProfileStudent {
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
            } else ProfileStudent(m, "Error", "Formato inválido", "", "", "", "")
        } catch (e: Exception) { ProfileStudent(m, "Error de red", "", "", "", "", "") }
    }

    // Carga académica
    override suspend fun traerCargaAcademica(): List<CargaAcademica> {
        return try {
            val xmlString = getCargaXml()
            val requestBody = xmlString.toRequestBody("text/xml; charset=utf-8".toMediaTypeOrNull())
            val response = snApiService.getCarga(requestBody)
            val rawJson = response.body()?.body?.getCargaResponse?.result ?: return emptyList()
            val cleanJson = if (rawJson.contains("<string")) rawJson.substringAfter(">").substringBeforeLast("</string>") else rawJson
            Gson().fromJson(cleanJson, object : TypeToken<List<CargaAcademica>>() {}.type)
        } catch (e: Exception) { emptyList() }
    }

    override fun obtenerCarga(): Flow<List<CargaAcademica>> = snDao.obtenerCarga()

    override suspend fun insertLocalCarga(materias: List<CargaAcademica>) {
        snDao.borrarCarga()
        snDao.insertarCarga(materias)
    }

    //KARDEX
    override suspend fun fetchKardexRemote(): List<Kardex> {
        return try {
            val xmlString = getKardexXml()
            val requestBody = xmlString.toRequestBody("text/xml; charset=utf-8".toMediaType())
            val response = snApiService.getKardex(requestBody)
            val xmlCompleto = response.string()
            val regex = Regex("""<getAllKardexConPromedioByAlumnoResult>([\s\S]*?)</getAllKardexConPromedioByAlumnoResult>""", RegexOption.IGNORE_CASE)
            val contenidoJson = regex.find(xmlCompleto)?.groupValues?.get(1) ?: ""
            if (contenidoJson.isNotEmpty()) parsearKardex(contenidoJson) else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    override fun obtenerKardexLocal(): Flow<List<Kardex>> = snDao.obtenerKardex()

    override suspend fun insertarKardexLocal(lista: List<Kardex>) {
        snDao.borrarKardex()
        snDao.insertarKardex(lista)
    }

    //NOTAS POR UNIDAD
    override suspend fun fetchNotasUnidadesRemote(): List<MateriaUnidades> {
        return try {
            val xmlString = getNotasUnidadesXml()
            val requestBody = xmlString.toRequestBody("text/xml; charset=utf-8".toMediaType())
            val response = snApiService.getNotasUnidades(requestBody)
            val xmlCompleto = response.string()
            val regex = Regex("""<getCalifUnidadesByAlumnoResult>([\s\S]*?)</getCalifUnidadesByAlumnoResult>""", RegexOption.IGNORE_CASE)
            val contenidoJson = regex.find(xmlCompleto)?.groupValues?.get(1) ?: ""
            if (contenidoJson.isNotEmpty() && contenidoJson != "null") parsearNotas(contenidoJson) else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    override fun obtenerNotasLocal(): Flow<List<MateriaUnidades>> = snDao.obtenerNotas()

    override suspend fun insertarNotasLocal(lista: List<MateriaUnidades>) {
        snDao.insertarNotas(lista)
    }

    //CALIFICACIONES FINALES
    override suspend fun fetchCalifFinalesRemote(): List<CalifFinal> {
        return try {
            val xmlString = getCalifFinalXml()
            val requestBody = xmlString.toRequestBody("text/xml; charset=utf-8".toMediaType())
            val response = snApiService.getCalifFinales(requestBody)
            val xmlCompleto = response.string()
            val regex = Regex("""<getAllCalifFinalByAlumnosResult>([\s\S]*?)</getAllCalifFinalByAlumnosResult>""", RegexOption.IGNORE_CASE)
            val contenidoJson = regex.find(xmlCompleto)?.groupValues?.get(1) ?: ""

            if (contenidoJson.isNotEmpty() && contenidoJson != "null") {
                val gson = Gson()
                val listaRaw: List<FinalRaw> = try {
                    val res = gson.fromJson(contenidoJson, FinalResponse::class.java)
                    res.lstCalificacionFinal
                } catch (e: Exception) {
                    gson.fromJson(contenidoJson, object : TypeToken<List<FinalRaw>>() {}.type)
                }
                listaRaw.map { raw ->
                    CalifFinal(materia = raw.materia ?: "", grupo = raw.grupo ?: "", calificacion = raw.calif ?: 0, acreditacion = raw.acred ?: "")
                }
            } else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    override fun obtenerFinalesLocal(): Flow<List<CalifFinal>> = snDao.obtenerFinales()

    override suspend fun insertarFinalesLocal(lista: List<CalifFinal>) {
        snDao.insertarFinales(lista)
    }

    //PARSEADORES INTERNOS
    private fun parsearKardex(jsonString: String): List<Kardex> {
        return try {
            val response: KardexResponse = Gson().fromJson(jsonString, KardexResponse::class.java)
            response.lstKardex.map { raw ->
                Kardex(
                    clvMateria = raw.ClvMat ?: "",
                    materia = raw.Materia ?: "",
                    calificacion = raw.Calif ?: 0,
                    acreditacion = raw.Acred ?: "",
                    periodo = "${raw.P1 ?: ""} ${raw.A1 ?: ""}".trim()
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parsearNotas(jsonString: String): List<MateriaUnidades> {
        return try {
            val gson = Gson()
            val listaRaw: List<UnidadesRaw> = try {
                gson.fromJson(jsonString, UnidadesResponse::class.java).lstCalificacionUnidades
            } catch (e: Exception) {
                gson.fromJson(jsonString, object : TypeToken<List<UnidadesRaw>>() {}.type)
            }
            listaRaw.map { raw ->
                val notas = listOfNotNull(raw.C1, raw.C2, raw.C3, raw.C4, raw.C5, raw.C6, raw.C7)
                    .joinToString(",") { if (it == "null" || it.isBlank()) "-" else it }
                MateriaUnidades(materia = raw.Materia ?: "Materia", unidades = notas)
            }
        } catch (e: Exception) { emptyList() }
    }
}