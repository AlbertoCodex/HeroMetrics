package com.example.herometrics.ui.screens.character

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herometrics.api.armory.AuthService
import com.example.herometrics.api.armory.CharacterViewData
import com.example.herometrics.api.armory.WowApiService
import com.example.herometrics.data.DataFirebase
import com.example.herometrics.data.DataPlayer
import kotlinx.coroutines.launch
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class CharacterViewModel(private val dataFirebase: DataFirebase) : ViewModel() {

    var characterViewData by mutableStateOf<CharacterViewData?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Armory Keys
    private val clientId = "0d15285a989f4e239683c9336288d6b5"
    private val clientSecret = "8uufmeKFCpvYiXDlgDIdp9GOCgCWDKjL"

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
                val statsResponse = wowService.getCharacterStats(
                    authHeader = "Bearer ${token.accessToken}",
                    realm = realm,
                    characterName = name
                )

                val profileResponse = wowService.getCharacterProfile(
                    authHeader = "Bearer ${token.accessToken}",
                    realm = realm,
                    characterName = name
                )

                val equipmentResponse = wowService.getCharacterEquipment(
                    authHeader = "Bearer ${token.accessToken}",
                    realm = realm,
                    characterName = name
                )

                val mediaResponse = wowService.getCharacterMedia(
                    authHeader = "Bearer ${token.accessToken}",
                    realm = realm,
                    characterName = name
                )

                val imageUrl = mediaResponse.assets.firstOrNull { it.key == "inset" }?.value

                // Clase modelo para la vista
                characterViewData = CharacterViewData(
                    name = profileResponse.name,
                    spec = profileResponse.active_spec?.name ?: "Desconocida",
                    itemLevel = equipmentResponse.equipped_item_level,
                    imageUrl = imageUrl ?: "",
                    crit = statsResponse.melee_crit.rating_normalized,
                    haste = statsResponse.melee_haste.rating_normalized,
                    mastery = statsResponse.mastery.rating_normalized,
                    versatility = statsResponse.versatility,
                    server = profileResponse.realm.slug
                )
                errorMessage = null

                guardarBusquedaReciente(
                    DataPlayer(
                        nombre = profileResponse.name,
                        servidor = profileResponse.realm.slug.capitalize()
                    )
                )

            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                characterViewData = null
            } finally {
                isLoading = false
            }
        }
    }

    fun guardarBusquedaReciente(player: DataPlayer) {
        dataFirebase.db.collection("busquedas")
            .whereEqualTo("nombre", player.nombre)
            .whereEqualTo("servidor", player.servidor)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    dataFirebase.db.collection("busquedas")
                        .add(
                            mapOf(
                                "nombre" to player.nombre,
                                "servidor" to player.servidor,
                            )
                        )
                }
            }
    }
}
