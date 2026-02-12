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


    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AddCookiesInterceptor(applicationContext))
            .addInterceptor(ReceivedCookiesInterceptor(applicationContext))
            .build()
    }


    private val retrofitSN: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrlSN)
            .client(client) // <-- Es vital que sea ESTE cliente con interceptores
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .build()
    }

    private val retrofitServiceSN: SICENETWService by lazy {
        retrofitSN.create(SICENETWService::class.java)
    }

    override val snRepository: SNRepository by lazy {
        NetworkSNRepository(retrofitServiceSN)
    }
}