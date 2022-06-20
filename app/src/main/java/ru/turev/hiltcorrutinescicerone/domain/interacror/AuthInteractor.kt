package ru.turev.hiltcorrutinescicerone.domain.interacror

import ru.turev.hiltcorrutinescicerone.data.auth.AppPreferences
import ru.turev.hiltcorrutinescicerone.data.repository.AuthRepositoryImpl
import ru.turev.hiltcorrutinescicerone.domain.errors.AuthResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInteractor @Inject constructor(
    private val authRepositoryImpl: AuthRepositoryImpl,
    private val appPreferences: AppPreferences,
    ) {

    suspend fun getNewAccessToken(code: String): AuthResponse {
        val response = authRepositoryImpl.getNewAccessToken(code, appPreferences.getAccessToken())
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            authRepositoryImpl.putNewAccessToken(body.accessToken)
            if (body.accessToken.isNotEmpty()) {
                profileInteractor.getUserProfile()
            }
            AuthResponse.Success
        } else {
            AuthResponse.Error(response.errorBody()?.string() ?: "Unknown error")
        }
    }

    fun userIsAuthorize(): Boolean = authRepositoryImpl.userIsAuthorize
}
