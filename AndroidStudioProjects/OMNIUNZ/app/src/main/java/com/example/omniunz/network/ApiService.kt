package com.example.omniunz.network

import User
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


private const val BASE_URL =
    "https://abex-e684e-default-rtdb.firebaseio.com/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()


interface ApiService {
    @GET("users.json")
    suspend fun getUser(): Map<String, User>

    @GET("users/{id}.json")
    suspend fun getUser(@Path("id") id: String): User

    @POST("users.json")
    suspend fun saveUser(@Body user: User)

    @DELETE("users/{id}.json")
    suspend fun deleteUser(@Path("id") id: String)

    @PUT("users/{id}.json")
    suspend fun putUser(@Path("id") id: String, @Body user: User): User


}



object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
