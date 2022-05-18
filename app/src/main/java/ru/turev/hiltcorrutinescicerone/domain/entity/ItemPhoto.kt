package ru.turev.hiltcorrutinescicerone.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemPhoto(
    val id: String,
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
    val smallS3: String,
    val likes: Int,
    val name: String
) : Parcelable
