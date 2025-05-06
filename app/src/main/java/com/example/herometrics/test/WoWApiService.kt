package com.example.herometrics.test

import retrofit2.http.*
import com.google.gson.annotations.SerializedName

interface WowApiService {
    @GET("profile/wow/character/{realm}/{characterName}/statistics")
    suspend fun getCharacterStats(
        @Header("Authorization") authHeader: String,
        @Path("realm") realm: String,
        @Path("characterName") characterName: String,
        @Query("namespace") namespace: String = "profile-eu",
        @Query("locale") locale: String = "es_ES"
    ): CharacterStatsResponse
}
