package ru.turev.hiltcorrutinescicerone.data.network.api.response

import com.google.gson.annotations.SerializedName
import ru.turev.hiltcorrutinescicerone.util.constants.Const

data class PhotoResponse(
    @SerializedName("id")
    val id: String = Const.STRING_EMPTY,
    @SerializedName("urls")
    val urls: Urls,
    @SerializedName("likes")
    val likes: Int = 0,
    @SerializedName("user")
    val user: User
)

class User {
    @SerializedName("name")
    val name: String = Const.STRING_EMPTY
}

data class Urls(
    @SerializedName("raw")
    val raw: String = Const.STRING_EMPTY,
    @SerializedName("full")
    val full: String = Const.STRING_EMPTY,
    @SerializedName("regular")
    val regular: String = Const.STRING_EMPTY,
    @SerializedName("small")
    val small: String = Const.STRING_EMPTY,
    @SerializedName("thumb")
    val thumb: String = Const.STRING_EMPTY,
    @SerializedName("small_s3")
    val smallS3: String = Const.STRING_EMPTY
)
