package ru.turev.hiltcorrutinescicerone.data.repository

import ru.turev.hiltcorrutinescicerone.data.network.api.AuthApi
import ru.turev.hiltcorrutinescicerone.data.network.api.request.GenerateAccessTokenRequest
import ru.turev.hiltcorrutinescicerone.data.network.api.request.UnsplashRequestTokens
import ru.turev.hiltcorrutinescicerone.data.network.api.response.GenerateAccessTokenResponse
import ru.turev.hiltcorrutinescicerone.domain.repository.AuthRepository
import ru.turev.hiltcorrutinescicerone.util.constants.Constants.GRANT_TYPE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun getNewAccessToken(
        code: String,
        unsplashRequestTokens: UnsplashRequestTokens
    ): GenerateAccessTokenResponse = authApi.generateAccessToken(
        GenerateAccessTokenRequest(
            unsplashRequestTokens.clientId,
            unsplashRequestTokens.clientSecret,
            unsplashRequestTokens.redirectUri,
            GRANT_TYPE,
            code
        )
    )
}
