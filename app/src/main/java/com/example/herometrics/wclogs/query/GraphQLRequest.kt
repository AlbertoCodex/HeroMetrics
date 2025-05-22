package com.example.herometrics.wclogs.query

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GraphQLRequest(val query: String)