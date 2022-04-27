package ru.turev.hiltcorrutinescicerone.data.network.api.response

import com.google.gson.annotations.SerializedName
import ru.turev.hiltcorrutinescicerone.util.empty

data class PhotoResponse(
    @SerializedName("id")
    val id: String = String.empty,
    @SerializedName("urls")
    val urls: Urls,
    @SerializedName("likes")
    val likes: Int = 0,
    @SerializedName("user")
    val user: User
)

class User {
    @SerializedName("name")
    val name: String = String.empty
}

data class Urls(
    @SerializedName("raw")
    val raw: String = String.empty,
    @SerializedName("full")
    val full: String = String.empty,
    @SerializedName("regular")
    val regular: String = String.empty,
    @SerializedName("small")
    val small: String = String.empty,
    @SerializedName("thumb")
    val thumb: String = String.empty,
    @SerializedName("small_s3")
    val smallS3: String = String.empty
)
