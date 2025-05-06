package com.example.herometrics.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CharacterViewModel : ViewModel() {

    var stats by mutableStateOf<CharacterStatsResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val clientId = "0d15285a989f4e239683c9336288d6b5"
    private val clientSecret = "8uufmeKFCpvYiXDlgDIdp9GOCgCWDKjL"

    init {
        fetchCharacterStats("stormscale", "zenys")
    }

    fun fetchCharacterStats(realm: String, name: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Interceptor para autenticación básica
                val authClient = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", Credentials.basic(clientId, clientSecret))
                            .build()
                        chain.proceed(request)
                    }
                    .build()

                val authRetrofit = Retrofit.Builder()
                    .baseUrl("https://eu.battle.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(authClient)
                    .build()

                val authService = authRetrofit.create(AuthService::class.java)
                val token = authService.getToken()

                // Cliente para llamadas a la API de WoW
                val apiRetrofit = Retrofit.Builder()
                    .baseUrl("https://eu.api.blizzard.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val wowService = apiRetrofit.create(WowApiService::class.java)
                stats = wowService.getCharacterStats(
                    authHeader = "Bearer ${token.accessToken}",
                    realm = realm,
                    characterName = name
                )
                errorMessage = null

            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                stats = null
            } finally {
                isLoading = false
            }
        }
    }
}
