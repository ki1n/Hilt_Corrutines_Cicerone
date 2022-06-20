package ru.turev.hiltcorrutinescicerone.data.network.api.response

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val username: String,
    val email: String
)
