package ru.turev.hiltcorrutinescicerone.data.network.api.response

import com.google.gson.annotations.SerializedName

data class GenerateAccessTokenResponse(@SerializedName("access_token") val accessToken: String)
