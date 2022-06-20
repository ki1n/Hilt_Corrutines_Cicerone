package ru.turev.hiltcorrutinescicerone.domain.repository

import ru.turev.hiltcorrutinescicerone.data.network.api.request.UnsplashRequestTokens
import ru.turev.hiltcorrutinescicerone.data.network.api.response.GenerateAccessTokenResponse

interface AuthRepository {

    suspend fun getNewAccessToken(
        code: String,
        unsplashRequestTokens: UnsplashRequestTokens
    ): GenerateAccessTokenResponse
}
