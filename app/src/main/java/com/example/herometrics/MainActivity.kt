package com.example.herometrics

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.herometrics.data.DataFirebase
import com.example.herometrics.navigation.AppNavigation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.herometrics.ui.theme.HeroMetricsTheme
import com.example.herometrics.api.wclogs.RetrofitClient
import com.example.herometrics.api.wclogs.query.GraphQLRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Instancia de Firebase
        val firebase = DataFirebase(Firebase.auth, Firebase.firestore)
        super.onCreate(savedInstanceState)
        setContent {
            HeroMetricsTheme {
                AppNavigation(firebase.auth, firebase.db)
                fetchTopSpecFromBoss("9ef8b85e-cf47-4ecb-8ed5-321a33bb0a08", "6DMDP3RjlSGjvQcCNmdLRIO5D9igxCuFpGdPB1eO", 3009, "Frost")
            }
        }
    }

    fun fetchTopSpecFromBoss(
        clientId: String,
        clientSecret: String,
        encounterId: Int,
        specFilter: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authService = RetrofitClient.createAuthService(clientId, clientSecret)
                val authResponse = authService.getToken()
                val token = "Bearer ${authResponse.access_token}"
                val gqlService = RetrofitClient.createGraphQLService()

                val filteredList = mutableListOf<JSONObject>()
                var page = 1
                var hasMorePages: Boolean

                do {
                    val query = """
                    {
                      worldData {
                        encounter(id: $encounterId) {
                          characterRankings(page: $page)
                        }
                      }
                    }
                """.trimIndent()

                    val response = gqlService.query(token, GraphQLRequest(query))

                    if (!response.isSuccessful) {
                        Log.e("ERROR", "GraphQL falló (página $page): ${response.code()} - ${response.errorBody()?.string()}")
                        return@launch
                    }

                    val body = response.body()?.string() ?: return@launch
                    Log.d("RAW_RESPONSE", "Página $page: ${body.take(300)}...") // Limita para no saturar log

                    val root = JSONObject(body)
                    val rawJson = root
                        .getJSONObject("data")
                        .getJSONObject("worldData")
                        .getJSONObject("encounter")
                        .get("characterRankings")

                    val rankingsJSON = JSONObject(rawJson.toString())
                    val rankingsArray = rankingsJSON.getJSONArray("rankings")
                    hasMorePages = rankingsJSON.optBoolean("hasMorePages", false)

                    for (i in 0 until rankingsArray.length()) {
                        val item = rankingsArray.getJSONObject(i)
                        if (item.getString("spec") == specFilter) {
                            filteredList.add(item)
                            if (filteredList.size >= 10) break
                        }
                    }

                    page++

                } while (hasMorePages && filteredList.size < 10)

                Log.d("DEBUG", "Filtrados: ${filteredList.size} resultados de spec '$specFilter'")

                val top10 = filteredList.sortedByDescending { it.getDouble("amount") }.take(10)

                top10.forEachIndexed { index, item ->
                    val name = item.getString("name")
                    val amount = item.getDouble("amount")
                    val cls = item.getString("class")
                    val spec = item.getString("spec")
                    Log.d("TOP10", "#${index + 1}: $name - $amount DPS ($cls $spec)")
                }

            } catch (e: Exception) {
                Log.e("EXCEPTION", "Error inesperado: ${e.stackTraceToString()}")
            }
        }
    }


}