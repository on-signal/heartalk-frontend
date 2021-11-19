package com.example.hatalk.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

private const val BASE_URL = "http://13.124.80.244:8000"

private val moshi = Moshi
    .Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit
    .Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    .baseUrl(BASE_URL)
    .build()

interface MatchingApiService{
    @POST("/room/entrance")
    suspend fun StartMatch(@Body body: MatchingRequest): Response<MatchingResponse>

    @POST("/room/queuecheck")
    suspend fun ConfirmMatch(@Body body:MatchingConfirmRequest): Response<MatchingConfirmResponse>

}

object MatchingApi {
    val retrofitService: MatchingApiService by lazy {
        retrofit.create(MatchingApiService::class.java)
    }
}