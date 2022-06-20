package ru.turev.hiltcorrutinescicerone.data.network.api.response

import com.google.gson.annotations.SerializedName

data class UserPhoto(@SerializedName("profile_image") val profileImage: String)
