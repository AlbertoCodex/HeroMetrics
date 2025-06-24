package com.example.herometrics.ui.screens.comparar

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herometrics.api.armory.AuthService
import com.example.herometrics.api.armory.CharacterStats
import com.example.herometrics.api.armory.CharacterViewData
import com.example.herometrics.api.armory.WowApiService
import com.example.herometrics.api.wclogs.RetrofitClient
import com.example.herometrics.api.wclogs.query.GraphQLRequest
import com.example.herometrics.data.DataPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.w3c.dom.CharacterData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompararViewModel : ViewModel() {

    private val clientId = "9ef8b85e-cf47-4ecb-8ed5-321a33bb0a08"
    private val clientSecret = "6DMDP3RjlSGjvQcCNmdLRIO5D9igxCuFpGdPB1eO"
    private val encounterId = 3009

    private val clientIdArmory = "0d15285a989f4e239683c9336288d6b5"
    private val secretIdArmory = "8uufmeKFCpvYiXDlgDIdp9GOCgCWDKjL"

    private val characterStatsList = mutableListOf<CharacterStats>()
    private val _selectedCharacters = MutableStateFlow<List<DataPlayer>>(emptyList())
    val selectedCharacters = _selectedCharacters.asStateFlow()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val _averageStats = MutableLiveData<CharacterStats>()
    val averageStats: LiveData<CharacterStats> = _averageStats

    fun fetchTopSpecFromBoss(specFilter: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val authService = RetrofitClient.createAuthService(clientId, clientSecret)
                val token = "Bearer ${authService.getToken().access_token}"
                val gqlService = RetrofitClient.createGraphQLService()

                val filteredList = mutableListOf<JSONObject>()
                var page = 1
                var hasMorePages: Boolean

                do {
                    val query = """
                        {
                          worldData {
                            encounter(id: $encounterId) {
                              characterRankings(page: $page, partition: 2)
                            }
                          }
                        }
                    """.trimIndent()

                    val response = gqlService.query(token, GraphQLRequest(query))
                    if (!response.isSuccessful) break

                    val body = response.body()?.string() ?: break
                    val rankingsArray = JSONObject(body)
                        .getJSONObject("data")
                        .getJSONObject("worldData")
                        .getJSONObject("encounter")
                        .getJSONObject("characterRankings")
                        .also { hasMorePages = it.optBoolean("hasMorePages", false) }
                        .getJSONArray("rankings")

                    for (i in 0 until rankingsArray.length()) {
                        val item = rankingsArray.getJSONObject(i)
                        if (item.getString("spec") == specFilter) {
                            filteredList.add(item)
                            if (filteredList.size >= 10) break
                        }
                    }

                    page++
                } while (hasMorePages && filteredList.size < 10)

                val top10 = filteredList.sortedByDescending { it.getDouble("amount") }.take(10)

                characterStatsList.clear()

                // Ejecuta las llamadas paralelamente y espera todas
                coroutineScope {
                    top10.map { item ->
                        async {
                            Log.d("CompararViewModel", "Contenido de item:\n${item.toString(2)}")
                            val name = item.getString("name")
                            val serverObj = item.getJSONObject("server")
                            val serverName = serverObj.getString("name")
                            val region = serverObj.getString("region")

                            if (region.equals("EU", ignoreCase = true)) {
                                addSelectedCharacter(DataPlayer(name.capitalize(), serverName.capitalize()))
                                fetchCharacterStats(serverName.lowercase(), name.lowercase())
                            }
                        }
                    }.awaitAll()
                }

                updateAverageStats()

            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun fetchCharacterStats(realm: String, name: String) {
        try {
            val authClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", Credentials.basic(clientIdArmory, secretIdArmory))
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

            val newStats = CharacterStats(
                crit = statsResponse.melee_crit.rating_normalized,
                haste = statsResponse.melee_haste.rating_normalized,
                mastery = statsResponse.mastery.rating_normalized,
                versatility = statsResponse.versatility
            )

            synchronized(characterStatsList) {
                characterStatsList.add(newStats)
            }

        } catch (e: Exception) {
            // Puedes loguear individualmente los fallos de personajes aquí si quieres
        }
    }

    private fun updateAverageStats() {
        if (characterStatsList.isEmpty()) return

        val total = characterStatsList.size
        val avgCrit = characterStatsList.sumOf { it.crit } / total
        val avgHaste = characterStatsList.sumOf { it.haste } / total
        val avgMastery = characterStatsList.sumOf { it.mastery } / total
        val avgVersatility = characterStatsList.sumOf { it.versatility } / total

        _averageStats.postValue(
            CharacterStats(
                crit = avgCrit,
                haste = avgHaste,
                mastery = avgMastery,
                versatility = avgVersatility
            )
        )
    }

    fun addSelectedCharacter(character: DataPlayer) {
        _selectedCharacters.value = _selectedCharacters.value + character
    }
}