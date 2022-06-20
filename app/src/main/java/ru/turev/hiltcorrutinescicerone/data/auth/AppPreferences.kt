package ru.turev.hiltcorrutinescicerone.data.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.turev.hiltcorrutinescicerone.data.network.api.response.UserProfile
import ru.turev.hiltcorrutinescicerone.domain.repository.AppPreferencesRepository
import ru.turev.hiltcorrutinescicerone.util.constants.Constants.STRING_EMPTY
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AppPreferencesRepository {

    companion object {
        const val UNSPLASH_REQUEST_TOKENS = "unsplash_request_tokens"
        const val RECENT_QUERIES = "queries"
        const val ACCESS_TOKEN = "access_token"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "email"
        const val PHOTO = "photo"
    }

    private fun putParams(params: Map<String, Any>) {
        sharedPreferences.edit {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Boolean -> putBoolean(key, value)
                }
            }
        }
    }

    private fun removeParams(params: List<String>) {
        sharedPreferences.edit {
            params.forEach(::remove)
        }
    }

    override fun setNewAccessToken(token: String) {
        putParams(mapOf(ACCESS_TOKEN to token))
    }

    override fun setUserProfile(userProfile: UserProfile) {
        putParams(
            mapOf(
                FIRST_NAME to userProfile.firstName,
                LAST_NAME to userProfile.lastName,
                USER_NAME to userProfile.username,
                USER_EMAIL to userProfile.email
            )
        )
    }

    override fun setUserPhotoUrl(url: String) {
        putParams(mapOf(PHOTO to url))
    }

    override fun getAccessToken(): String = sharedPreferences.getString(ACCESS_TOKEN, STRING_EMPTY).orEmpty()

    override fun getUserProfile(): UserProfile? {
        val firstName = sharedPreferences.getString(FIRST_NAME, "") ?: ""
        val lastName = sharedPreferences.getString(LAST_NAME, "") ?: ""
        val username = sharedPreferences.getString(USER_NAME, "") ?: ""
        val email = sharedPreferences.getString(USER_EMAIL, "") ?: ""

        return if (firstName.isNotEmpty() && lastName.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty()) UserProfile(
            firstName,
            lastName,
            username,
            email
        ) else null
    }

    override fun getUserPhotoUrl(): String = sharedPreferences.getString(PHOTO, STRING_EMPTY).orEmpty()

    override fun isSignUp(): Boolean = when {
        sharedPreferences.getString(FIRST_NAME, "") == "" -> false
        sharedPreferences.getString(LAST_NAME, "") == "" -> false
        sharedPreferences.getString(USER_NAME, "") == "" -> false
        sharedPreferences.getString(USER_EMAIL, "") == "" -> false
        else -> true
    }
}
