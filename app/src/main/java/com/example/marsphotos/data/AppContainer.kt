
package com.example.marsphotos.data

import AddCookiesInterceptor
import ReceivedCookiesInterceptor
import android.content.Context
import com.example.marsphotos.network.SICENETWService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory



interface AppContainer {


    val snRepository: SNRepository
}


class DefaultAppContainer(applicationContext: Context) : AppContainer {
    private val baseUrl = "https://android-kotlin-fun-mars-server.appspot.com/"
    private val baseUrlSN = "https://sicenet.surguanajuato.tecnm.mx"
    private var client: OkHttpClient
    init {
        client = OkHttpClient()
        val builder = OkHttpClient.Builder()

        builder.addInterceptor(AddCookiesInterceptor(applicationContext)) // VERY VERY IMPORTANT

        builder.addInterceptor(ReceivedCookiesInterceptor(applicationContext)) // VERY VERY IMPORTANT

        client = builder.build()
    }
    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitSN: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrlSN)
        .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
        .client(client)
        .build()

    //bodyacceso.toRequestBody("text/xml; charset=utf-8".toMediaType())

    /**
     * Retrofit service object for creating api calls
     */


    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitServiceSN: SICENETWService by lazy {
        retrofitSN.create(SICENETWService::class.java)
    }

    /**
     * DI implementation for Mars photos repository
     */
    override val snRepository: NetworSNRepository by lazy {
        NetworSNRepository(retrofitServiceSN)
    }
}
