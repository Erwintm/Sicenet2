package com.example.marsphotos.ui.screens

import android.util.Log

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var usuario by mutableStateOf("")
    var password by mutableStateOf("")
    var mensajeError by mutableStateOf<String?>(null)

    fun login() {
        if (usuario.isBlank() || password.isBlank()) {
            mensajeError = "Campos obligatorios"
            return
        }

        // Aquí validas usuario/contraseña
        mensajeError = null
        Log.d("LOGIN", "Login correcto")
    }
}


