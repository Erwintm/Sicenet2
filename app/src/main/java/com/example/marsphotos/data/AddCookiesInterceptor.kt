package com.example.marsphotos.network

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AddCookiesInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        // Recuperamos el set de cookies guardadas
        val cookies = prefs.getStringSet(PREF_COOKIES, HashSet()) ?: HashSet()

        if (cookies.isNotEmpty()) {
            // BUSCAMOS ESPECÍFICAMENTE LA SESIÓN DE ASP.NET
            // Esta es la que nos da permiso de ver el Kardex
            val sessionCookie = cookies.find { it.contains("ASP.NET_SessionId", ignoreCase = true) }

            if (sessionCookie != null) {
                // Si la encontramos, la mandamos LIMPIA (Nombre=Valor)
                val cleanCookie = sessionCookie.split(";")[0]
                builder.addHeader("Cookie", cleanCookie)
                Log.d("COOKIES_REPORTE", ">>> MANDANDO SESIÓN (ELIMINA ERROR 500): $cleanCookie")
            } else {
                // Si no hay sesión (porque apenas nos vamos a loguear), mandamos la anónima
                val anonymousCookie = cookies.first().split(";")[0]
                builder.addHeader("Cookie", anonymousCookie)
                Log.d("COOKIES_REPORTE", ">>> MANDANDO COOKIE INICIAL: $anonymousCookie")
            }
        } else {
            Log.w("COOKIES_REPORTE", ">>> SIN COOKIES: Petición enviada limpia.")
        }

        return chain.proceed(builder.build())
    }

    companion object {
        const val PREF_COOKIES = "PREF_COOKIES"
    }
}