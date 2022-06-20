package ru.turev.hiltcorrutinescicerone.data.network.api.request

import com.google.gson.annotations.SerializedName

data class GenerateAccessTokenRequest(
    @SerializedName("client_id") val clientId: String,
    @SerializedName("client_secret") val clientSecret: String,
    @SerializedName("redirect_uri") val redirectUri: String,
    @SerializedName("grant_type") val grantType: String,
    val code: String
)
