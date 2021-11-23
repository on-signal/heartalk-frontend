package com.example.hatalk.network

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.lang.Exception

private const val BASE_URL = "http://3.18.104.98:8000"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit =
    Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(
        BASE_URL
    ).build()

interface UserApiService {
    @POST("users/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("users/signup")
    suspend fun signUp(@Body body: SignUpRequest): Response<SignUpResponse>

    @GET("users")
    suspend fun getCurrentUser(@Header("Authorization") jwt: String): Response<GetProfileResponse>
}

object UserApi {
    val retrofitService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}