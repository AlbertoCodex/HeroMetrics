package com.example.herometrics.api.wclogs

import com.example.herometrics.api.wclogs.query.GraphQLService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit

object RetrofitClient {

    private const val BASE_URL = "https://www.warcraftlogs.com/"

    fun createAuthService(clientId: String, clientSecret: String): AuthService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val authInterceptor = Interceptor { chain ->
            val credentials = Credentials.basic(clientId, clientSecret)
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", credentials)
                .build()
            chain.proceed(newRequest)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AuthService::class.java)
    }

    fun createGraphQLService(): GraphQLService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GraphQLService::class.java)
    }
}
