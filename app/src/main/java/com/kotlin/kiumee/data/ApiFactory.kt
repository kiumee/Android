package com.kotlin.kiumee.data

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kotlin.kiumee.BuildConfig.BASE_URL
import com.kotlin.kiumee.MainApplication
import com.kotlin.kiumee.data.api.HomeApiService
import com.kotlin.kiumee.data.api.LoginApiService
import com.kotlin.kiumee.data.api.MenuApiService
import com.kotlin.kiumee.data.api.StoreApiService
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.IOException

object ApiFactory {
    private fun getLogOkHttpClient(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("Retrofit2", "CONNECTION INFO -> $message")
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    private fun okHttpClient(interceptor: Interceptor) = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(getLogOkHttpClient())
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient(AppInterceptor()))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create<T>(T::class.java)

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val accessToken = MainApplication.prefs.getAccessToken()
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            proceed(newRequest)
        }
    }
}

object ServicePool {
    val loginApiService = ApiFactory.create<LoginApiService>()
    val storeApiService = ApiFactory.create<StoreApiService>()
    val menuApiService = ApiFactory.create<MenuApiService>()
    val homeApiService = ApiFactory.create<HomeApiService>()
}
