package com.example.ttmrcan

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCliente{
    val webService : WebService by lazy {
        Retrofit.Builder()
            .baseUrl(Constantes.BASE_URL_IMAGEN)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebService::class.java)
    }
}