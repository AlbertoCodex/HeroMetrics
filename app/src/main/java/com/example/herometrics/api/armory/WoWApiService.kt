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

    @GET("profile/wow/character/{realm}/{characterName}")
    suspend fun getCharacterProfile(
        @Header("Authorization") authHeader: String,
        @Path("realm") realm: String,
        @Path("characterName") characterName: String,
        @Query("namespace") namespace: String = "profile-eu",
        @Query("locale") locale: String = "en_GB"
    ): CharacterProfileResponse

    @GET("profile/wow/character/{realm}/{characterName}")
    suspend fun getCharacterEquipment(
        @Header("Authorization") authHeader: String,
        @Path("realm") realm: String,
        @Path("characterName") characterName: String,
        @Query("namespace") namespace: String = "profile-eu",
        @Query("locale") locale: String = "es_ES"
    ): CharacterEquipmentResponse

    @GET("profile/wow/character/{realm}/{characterName}/character-media")
    suspend fun getCharacterMedia(
        @Header("Authorization") authHeader: String,
        @Path("realm") realm: String,
        @Path("characterName") characterName: String,
        @Query("namespace") namespace: String = "profile-eu",
        @Query("locale") locale: String = "es_ES"
    ): CharacterMediaResponse

}