package com.example.marsphotos.network // Asegúrate que el package coincida con tu proyecto

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ReceivedCookiesInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())

        // Si la respuesta trae el encabezado "Set-Cookie", las guardamos
        // Dentro de tu interceptor, busca la parte donde guardas y cámbiala por esta:
        // Dentro de tu intercept() de ReceivedCookiesInterceptor
        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            val cookies = originalResponse.headers("Set-Cookie").toMutableSet()

            val success = PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putStringSet(AddCookiesInterceptor.PREF_COOKIES, cookies)
                .commit() // COMMIT es síncrono, asegura que se guarde YA.

            Log.d("COOKIES_REPORTE", "COOKIES GUARDADAS: $success")
        }

        return originalResponse
    }
}