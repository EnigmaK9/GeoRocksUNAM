package com.enigma.georocks.data.remote

import com.enigma.georocks.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    fun getRetrofit(): Retrofit {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Nivel de detalle de los logs
        }

        val client = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // Asegúrate de que este sea el URL base correcto
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Convertidor para JSON
            .build()
    }
}
