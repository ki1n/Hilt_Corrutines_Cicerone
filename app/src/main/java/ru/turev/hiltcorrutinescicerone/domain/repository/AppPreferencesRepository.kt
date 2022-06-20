package ru.turev.hiltcorrutinescicerone.domain.repository

import ru.turev.hiltcorrutinescicerone.data.network.api.response.UserProfile

interface AppPreferencesRepository {

    fun setNewAccessToken(token: String)
    fun setUserProfile(userProfile: UserProfile)
    fun setUserPhotoUrl(url: String)

    fun getAccessToken(): String
    fun getUserProfile(): UserProfile?
    fun getUserPhotoUrl(): String

    fun isSignUp(): Boolean
}
