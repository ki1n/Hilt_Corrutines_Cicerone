package ru.turev.hiltcorrutinescicerone.data.network.api.response

import com.google.gson.annotations.SerializedName
import ru.turev.hiltcorrutinescicerone.util.constants.Constants

data class PhotoResponse(
    @SerializedName("id")
    val id: String = Constants.STRING_EMPTY,
    @SerializedName("urls")
    val urls: Urls,
    @SerializedName("likes")
    val likes: Int = 0,
    @SerializedName("user")
    val user: User
)

class User {
    @SerializedName("name")
    val name: String = Constants.STRING_EMPTY
}

data class Urls(
    @SerializedName("raw")
    val raw: String = Constants.STRING_EMPTY,
    @SerializedName("full")
    val full: String = Constants.STRING_EMPTY,
    @SerializedName("regular")
    val regular: String = Constants.STRING_EMPTY,
    @SerializedName("small")
    val small: String = Constants.STRING_EMPTY,
    @SerializedName("thumb")
    val thumb: String = Constants.STRING_EMPTY,
    @SerializedName("small_s3")
    val smallS3: String = Constants.STRING_EMPTY
)
