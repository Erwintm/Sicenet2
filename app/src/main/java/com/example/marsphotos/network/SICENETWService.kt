package com.example.marsphotos.network

import com.example.marsphotos.data.CargaSoapResponse
import com.example.marsphotos.model.EnvelopeCarga
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Response


// --- FUNCIONES GENERADORAS DE XML ---

fun getLoginXml(matricula: String, nip: String): String = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <accesoLogin xmlns="http://tempuri.org/">
          <strMatricula>$matricula</strMatricula>
          <strContrasenia>$nip</strContrasenia>
          <tipoUsuario>ALUMNO</tipoUsuario>
        </accesoLogin>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

fun getCargaXml(): String = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

fun getPerfilXml(matricula: String): String = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/">
          <strMatricula>$matricula</strMatricula>
        </getAlumnoAcademicoWithLineamiento>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

fun getKardexXml(): String = """
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
      <aluLineamiento>1</aluLineamiento> 
    </getAllKardexConPromedioByAlumno>
  </soap:Body>
</soap:Envelope>
""".trimIndent()

fun getCalifFinalXml(modEducativo: Int = 1): String = """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
      <bytModEducativo>$modEducativo</bytModEducativo>
    </getAllCalifFinalByAlumnos>
  </soap:Body>
</soap:Envelope>""".trimIndent()

fun getNotasUnidadesXml(): String = """<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
  </soap:Body>
</soap:Envelope>""".trimIndent()


// --- INTERFAZ DEL SERVICIO ---

interface SICENETWService {
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    @POST("ws/wsalumnos.asmx")
    suspend fun acceso(@Body soap: RequestBody): ResponseBody

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAlumnoAcademicoWithLineamiento\""
    )
    @POST("ws/wsalumnos.asmx")
    suspend fun getPerfil(@Body soap: RequestBody): ResponseBody

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getCargaAcademicaByAlumno\""
    )
    @POST("ws/wsalumnos.asmx")
    suspend fun getCarga(@Body body: RequestBody): Response<CargaSoapResponse>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAllKardexConPromedioByAlumno\""
    )
    // En SICENETWService.kt debe quedar así:

    @POST("ws/wsalumnos.asmx")
    suspend fun getKardex(@Body soap: RequestBody): okhttp3.ResponseBody

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAllCalifFinalByAlumnos\""
    )
    @POST("ws/wsalumnos.asmx")
    suspend fun getCalifFinales(@Body soap: RequestBody): ResponseBody

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getCalifUnidadesByAlumno\""
    )
    @POST("ws/wsalumnos.asmx")
    suspend fun getNotasUnidades(@Body soap: RequestBody): ResponseBody
}