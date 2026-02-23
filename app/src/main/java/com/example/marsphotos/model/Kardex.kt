package com.example.marsphotos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kardex")
data class Kardex(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clvMateria: String = "",
    val materia: String = "",
    val calificacion: Int = 0,
    val acreditacion: String = "", // Ejemplo: "ORDINARIO"
    val periodo: String = "",      // Ejemplo: "AGO-DIC 2023"
    var fechaSincronizacion: String = "" // Requerido por el profe
)