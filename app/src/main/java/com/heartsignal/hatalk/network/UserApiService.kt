package com.heartsignal.hatalk.network

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.heartsignal.hatalk.main.data.ChatData
import com.heartsignal.hatalk.main.data.ChatMessage
import com.heartsignal.hatalk.main.data.Friends
import com.heartsignal.hatalk.signalRoom.PRIVATE.URLs
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.lang.Exception

import com.heartsignal.hatalk.main.userModel.UserModel
import retrofit2.Call
import retrofit2.http.GET



private const val BASE_URL = URLs.URL

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

    @POST("users/delete")
    suspend fun deleteUser(@Header("Authorization") jwt: String, @Body body: DeleteUserRequest): Response<DeleteUserResponse>

    @GET("chats/{userId}")
    suspend fun getUserFriend(@Path(value = "userId", encoded = true)userId: String): Response<Array<Friends>>

    @GET("chats/user/{roomId}")
    suspend fun getChatMessages(@Path(value = "roomId", encoded = true) roomId: String): Response<ChatData>

    @PATCH("users")
    suspend fun updateUser(@Header("Authorization") jwt: String, @Body body: UpdateUserRequest): Response<UpdateUserResponse>
}

object UserApi {
    val retrofitService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}