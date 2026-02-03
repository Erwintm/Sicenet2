package com.example.marsphotos.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Generación del XML para Login
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

// Generación del XML para Perfil - Corregido a strMatricula
// 1. Corregimos el nombre del método en el XML
fun getPerfilXml(matricula: String): String = """
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/">
      <strMatricula>$matricula</strMatricula>
    </getAlumnoAcademicoWithLineamiento>
  </soap:Body>
</soap:Envelope>
""".trim()

interface SICENETWService {
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    @POST("/ws/wsalumnos.asmx")
    suspend fun acceso(@Body soap: RequestBody): ResponseBody

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        // 2. Corregimos el Header con el nombre real del método
        "SOAPAction: \"http://tempuri.org/getAlumnoAcademicoWithLineamiento\""
    )
    @POST("/ws/wsalumnos.asmx")
    suspend fun getPerfil(@Body soap: RequestBody): ResponseBody
}