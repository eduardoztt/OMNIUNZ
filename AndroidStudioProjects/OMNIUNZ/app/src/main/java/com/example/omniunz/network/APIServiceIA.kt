package com.example.omniunz.network

import ImageRequest
import PredictionResult
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://cool-bay-443501-s5.rj.r.appspot.com"

// Configurando cliente OkHttp com timeouts personalizados
private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(120, TimeUnit.SECONDS)
    .readTimeout(120, TimeUnit.SECONDS)
    .writeTimeout(120, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        var request = chain.request()
        var response = chain.proceed(request)
        var retryCount = 0
        while (!response.isSuccessful && retryCount < 3) {
            retryCount++
            response = chain.proceed(request)
        }
        response
    }
    .build()


// Criando instância do Retrofit
private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

// Definição da interface da API
interface ApiServiceIA {
    @POST("/predict")
    suspend fun image(@Body image: ImageRequest): List<PredictionResult>
}

// Implementação singleton do serviço
object ApiAlimento {
    val retrofitService: ApiServiceIA by lazy {
        retrofit.create(ApiServiceIA::class.java)
    }
}

