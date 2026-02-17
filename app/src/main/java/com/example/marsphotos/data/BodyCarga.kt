package com.example.marsphotos.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Body", strict = false)
data class BodyCarga(
    @field:Element(name = "getCargaAcademicaByAlumnoResponse", required = false)
    var response: ResponseCarga? = null
)
