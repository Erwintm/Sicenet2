package com.example.marsphotos.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Envelope", strict = false)
data class EnvelopeCarga(
    @field:Element(name = "Body")
    var body: BodyCarga? = null
)
