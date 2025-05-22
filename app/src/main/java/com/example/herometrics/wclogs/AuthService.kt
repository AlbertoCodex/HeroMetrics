package com.example.herometrics.wclogs

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Field("grant_type") grantType: String = "client_credentials"
    ): AuthResponse
}