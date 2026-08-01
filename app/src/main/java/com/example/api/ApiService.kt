package com.example.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.BuildConfig
import java.util.concurrent.TimeUnit

interface NeoxrApi {
    @GET("api/youtube")
    suspend fun getYoutube(
        @Query("url") url: String,
        @Query("type") type: String, // "video" or "audio"
        @Query("quality") quality: String? = null, // "1080p", etc.
        @Query("apikey") apiKey: String = BuildConfig.NEOXR_API_KEY
    ): Map<String, Any>

    @GET("api/tiktok")
    suspend fun getTiktok(
        @Query("url") url: String,
        @Query("apikey") apiKey: String = BuildConfig.NEOXR_API_KEY
    ): Map<String, Any>

    @GET("api/ig")
    suspend fun getInstagram(
        @Query("url") url: String,
        @Query("apikey") apiKey: String = BuildConfig.NEOXR_API_KEY
    ): Map<String, Any>

    @GET("api/spotify-search")
    suspend fun searchSpotify(
        @Query("q") query: String,
        @Query("apikey") apiKey: String = BuildConfig.NEOXR_API_KEY
    ): SpotifySearchResponse

    @GET("api/pin")
    suspend fun getPinterest(
        @Query("url") url: String,
        @Query("apikey") apiKey: String = BuildConfig.NEOXR_API_KEY
    ): Map<String, Any>
}

interface BotcahxApi {
    @GET("api/download/spotify2")
    suspend fun downloadSpotify(
        @Query("url") url: String,
        @Query("apikey") apiKey: String = BuildConfig.BOTCAHX_API_KEY
    ): SpotifyDownloadResponse
}

data class SpotifySearchResponse(
    val status: Boolean,
    val data: List<SpotifySearchItem>?
)

data class SpotifySearchItem(
    val title: String,
    val artist: String?,
    val author: String?,
    val thumbnail: String?,
    val image: String?,
    val url: String,
    val duration: String?
)

data class SpotifyDownloadResponse(
    val status: Boolean,
    val result: SpotifyDownloadResult?
)

data class SpotifyDownloadResult(
    val data: SpotifyDownloadData?
)

data class SpotifyDownloadData(
    val url: String
)

object ApiClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val neoxrApi: NeoxrApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.neoxr.eu/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NeoxrApi::class.java)
    }

    val botcahxApi: BotcahxApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.botcahx.eu.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BotcahxApi::class.java)
    }
}
