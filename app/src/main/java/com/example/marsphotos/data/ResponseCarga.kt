package com.example.marsphotos.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "getCargaAcademicaByAlumnoResponse", strict = false)
data class ResponseCarga(
    @field:Element(name = "getCargaAcademicaByAlumnoResult", required = false)
    var result: String? = null
)
