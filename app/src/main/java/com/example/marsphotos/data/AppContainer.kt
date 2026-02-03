package com.example.marsphotos.data

import AddCookiesInterceptor
import ReceivedCookiesInterceptor
import android.content.Context
import com.example.marsphotos.network.SICENETWService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory // Agregamos esto
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

interface AppContainer {
    val snRepository: SNRepository
}

class DefaultAppContainer(applicationContext: Context) : AppContainer {
    private val baseUrlSN = "https://sicenet.surguanajuato.tecnm.mx"
    private var client: OkHttpClient

    init {
        // Configuramos el cliente con tus interceptores de Cookies (Punto 1 y 6)
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(AddCookiesInterceptor(applicationContext))
        builder.addInterceptor(ReceivedCookiesInterceptor(applicationContext))
        client = builder.build()
    }

    // Usamos ScalarsConverterFactory para manejar el XML de SICENET de forma flexible
    private val retrofitSN: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrlSN)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
        .client(client)
        .build()

    private val retrofitServiceSN: SICENETWService by lazy {
        retrofitSN.create(SICENETWService::class.java)
    }

    override val snRepository: SNRepository by lazy {
        NetworSNRepository(retrofitServiceSN)
    }
}