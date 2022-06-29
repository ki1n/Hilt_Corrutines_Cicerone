package ru.turev.hiltcorrutinescicerone.domain.repository

import okhttp3.ResponseBody
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhotoPage
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource

interface PhotoRepository {

    suspend fun getAllPhotos(page: Int): Resource<ItemPhotoPage>

    suspend fun getSearchPhotos(query: String): Resource<List<ItemPhoto>>

    suspend fun getBitmapFull(urlFull: String): ResponseBody
}
