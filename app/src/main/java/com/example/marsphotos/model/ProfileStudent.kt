package com.example.marsphotos.model

// Data class lista para recibir los datos del perfil
data class ProfileStudent(
    val matricula: String = "",
    val nombre: String = "",
    val carrera: String = "",
    val promedio: String = "",
    val semestre: String = ""
)