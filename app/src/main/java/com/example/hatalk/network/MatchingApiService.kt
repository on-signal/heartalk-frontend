package com.example.hatalk.network

import android.database.Observable
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import java.lang.reflect.Type
import java.util.*

val nullOnEmptyConverterFactory = object : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
        val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
        override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
    }
}

private const val BASE_URL = "http://143.248.200.21:8000"

private val moshi = Moshi
    .Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit
    .Builder()
    .addConverterFactory(nullOnEmptyConverterFactory)
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    .baseUrl(BASE_URL)
    .build()

interface MatchingApiService{
    @POST("/room/entrance")
    suspend fun StartMatch(@Body body: MatchingRequest): Response<MatchingResponse>

    @POST("/room/checkqueue")
    suspend fun ConfirmMatch(@Body body:MatchingConfirmRequest): Response<MatchingConfirmResponse>

    @DELETE("/room/one/delete")
    suspend fun deleteRoom(@Body body: DeleteRoomRequest): Response<DeleteRoomResponse>

}

object MatchingApi {
    val retrofitService: MatchingApiService by lazy {
        retrofit.create(MatchingApiService::class.java)
    }
}
