package com.example.marsphotos.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// XML para el Login
val bodyacceso = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <accesoLogin xmlns="http://tempuri.org/">
          <strMatricula>%s</strMatricula>
          <strContrasenia>%s</strContrasenia>   
          <tipoUsuario>ALUMNO</tipoUsuario>
        </accesoLogin>
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

// XML para el Perfil (Punto 2 de tu evaluación)
// Este método no suele pedir parámetros porque usa la Cookie de sesión
val bodyperfil = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getResumenAcademicoAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
""".trimIndent()

interface SICENETWService {

    @Headers(
        "Content-Type: text/xml;charset=utf-8",
        "SOAPAction: http://tempuri.org/accesoLogin"
    )
    @POST("/ws/wsalumnos.asmx")
    suspend fun acceso(@Body soap: RequestBody): ResponseBody

    @Headers(
        "Content-Type: text/xml;charset=utf-8",
        "SOAPAction: http://tempuri.org/getResumenAcademicoAlumno"
    )
    @POST("/ws/wsalumnos.asmx")
    suspend fun getPerfil(@Body soap: RequestBody): ResponseBody
}