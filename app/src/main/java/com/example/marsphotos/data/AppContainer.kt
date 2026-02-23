package com.example.marsphotos.data

import android.content.Context
// Cambiamos los imports para que coincidan con el paquete de los interceptores
import com.example.marsphotos.network.AddCookiesInterceptor
import com.example.marsphotos.network.ReceivedCookiesInterceptor
import com.example.marsphotos.network.SICENETWService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

interface AppContainer {
    val snRepository: SNRepository
}

class DefaultAppContainer(private val applicationContext: Context) : AppContainer {
    private val baseUrlSN = "https://sicenet.surguanajuato.tecnm.mx"


    private val database: SNDatabase by lazy {
        SNDatabase.getDatabase(applicationContext)
    }


    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            // Mantenemos tus interceptores por si los usas para logs,
            // pero el CookieJar será el que mande realmente.
            .addInterceptor(AddCookiesInterceptor(applicationContext))
            .addInterceptor(ReceivedCookiesInterceptor(applicationContext))
            // AGREGA ESTO: Un manejador de cookies persistente en memoria
            .cookieJar(object : okhttp3.CookieJar {
                private val cookieStore = HashMap<okhttp3.HttpUrl, List<okhttp3.Cookie>>()

                override fun saveFromResponse(url: okhttp3.HttpUrl, cookies: List<okhttp3.Cookie>) {
                    cookieStore[url] = cookies
                }

                override fun loadForRequest(url: okhttp3.HttpUrl): List<okhttp3.Cookie> {
                    return cookieStore[url] ?: ArrayList()
                }
            })
            .build()
    }


    private val retrofitSN: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrlSN)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())

            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .build()
    }

    private val retrofitServiceSN: SICENETWService by lazy {
        retrofitSN.create(SICENETWService::class.java)
    }


    override val snRepository: SNRepository by lazy {
        NetworkSNRepository(retrofitServiceSN, database.cargaDao())
    }


    val wmRepository: SNWMRepository by lazy {
        WorkManagerSNWMRepository(applicationContext)
    }
}