package com.example.marsphotos.network // Asegúrate que el package coincida con tu proyecto

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ReceivedCookiesInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    // En ReceivedCookiesInterceptor.kt
    // En ReceivedCookiesInterceptor.kt
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val setCookieHeaders = originalResponse.headers("Set-Cookie")

        if (setCookieHeaders.isNotEmpty()) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            // 1. Recuperamos las que ya tenemos guardadas
            val existingCookies = prefs.getStringSet(AddCookiesInterceptor.PREF_COOKIES, HashSet()) ?: HashSet()
            val newCookies = HashSet<String>(existingCookies)

            // 2. Solo agregamos, NO reemplazamos todo
            for (header in setCookieHeaders) {
                newCookies.add(header)
            }

            // 3. Guardado atómico
            prefs.edit().putStringSet(AddCookiesInterceptor.PREF_COOKIES, newCookies).commit()
            Log.d("COOKIES_REPORTE", "COOKIES ACTUALIZADAS: ${newCookies.size} en total")
        }

        return originalResponse
    }
}