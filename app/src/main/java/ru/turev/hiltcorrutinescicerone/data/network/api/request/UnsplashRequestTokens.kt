package ru.turev.hiltcorrutinescicerone.data.network.api.request

data class UnsplashRequestTokens(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String
)
