package ru.turev.hiltcorrutinescicerone.data.network.api

import retrofit2.http.Body
import retrofit2.http.POST
import ru.turev.hiltcorrutinescicerone.data.network.api.request.GenerateAccessTokenRequest
import ru.turev.hiltcorrutinescicerone.data.network.api.response.GenerateAccessTokenResponse

interface AuthApi {

    @POST("oauth/token")
    suspend fun generateAccessToken(@Body requestBody: GenerateAccessTokenRequest): GenerateAccessTokenResponse
}
