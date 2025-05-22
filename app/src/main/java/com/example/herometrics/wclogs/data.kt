package com.example.herometrics.wclogs

import com.google.gson.JsonObject

data class RankingResponseWrapper(
    val data: WorldDataContainer
)

data class WorldDataContainer(
    val worldData: EncounterData
)

data class EncounterData(
    val encounter: EncounterRankings
)

data class EncounterRankings(
    val characterRankings: JsonObject  // <- lo parseamos manualmente luego
)

data class SingleRanking(
    val name: String,
    val amount: Double,
    val `class`: String,
    val spec: String,
    val report: ReportCode
)

data class ReportCode(
    val code: String
)