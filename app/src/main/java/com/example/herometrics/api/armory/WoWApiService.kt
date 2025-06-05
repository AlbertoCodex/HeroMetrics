package com.example.herometrics.api.armory

import retrofit2.http.*

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
