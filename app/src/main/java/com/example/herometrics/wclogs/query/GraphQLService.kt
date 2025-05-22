package com.example.herometrics.wclogs.query

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GraphQLService {
    @POST("api/v2/client")
    suspend fun query(
        @Header("Authorization") token: String,
        @Body request: GraphQLRequest
    ): Response<ResponseBody> // o puedes definir un DTO personalizado
}