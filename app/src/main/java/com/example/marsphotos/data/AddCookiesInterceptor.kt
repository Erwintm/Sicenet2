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


        val cookies = prefs.getStringSet(PREF_COOKIES, null)

        if (!cookies.isNullOrEmpty()) {
            val cookieString = cookies.joinToString(separator = "; ") { it.split(";")[0] }
            builder.addHeader("Cookie", cookieString)

            Log.d("COOKIES_REPORTE", "ENVIANDO AL SERVIDOR: $cookieString")
        } else {
            Log.e("COOKIES_REPORTE", "¡ATENCIÓN! No hay cookies para enviar.")
        }

        return chain.proceed(builder.build())
    }

    companion object {
        const val PREF_COOKIES = "PREF_COOKIES"
    }
}