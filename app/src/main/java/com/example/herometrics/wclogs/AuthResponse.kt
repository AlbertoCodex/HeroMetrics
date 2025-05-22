package com.example.herometrics.wclogs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)
