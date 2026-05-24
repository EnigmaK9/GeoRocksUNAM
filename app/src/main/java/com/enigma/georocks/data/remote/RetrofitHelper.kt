package com.enigma.georocks.data.remote

import android.content.Context
import com.enigma.georocks.utils.Constants
import com.enigma.georocks.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper(private val context: Context? = null) {
    fun getRetrofit(): Retrofit {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log detail level
        }

        val client = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            
            // Add authorization interceptor if context is provided
            context?.let { ctx ->
                val sessionManager = SessionManager(ctx)
                addInterceptor(Interceptor { chain ->
                    val original = chain.request()
                    val token = sessionManager.getAuthToken()
                    
                    if (!token.isNullOrBlank()) {
                        val requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer $token")
                            .method(original.method, original.body)
                        chain.proceed(requestBuilder.build())
                    } else {
                        chain.proceed(original)
                    }
                })
            }
        }.build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // correct base URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Converter for JSON
            .build()
    }
}
