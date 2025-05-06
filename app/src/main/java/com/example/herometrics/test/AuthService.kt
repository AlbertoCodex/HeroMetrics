package com.example.herometrics.test

import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.annotations.SerializedName

interface AuthService {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Field("grant_type") grantType: String = "client_credentials"
    ): AuthToken
}

data class AuthToken(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("token_type") val tokenType: String
)
