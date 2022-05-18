package ru.turev.hiltcorrutinescicerone.domain.repository

import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource

interface PhotoRepository {

    suspend fun getAllPhotos(perPage: Int): Resource<List<ItemPhoto>>

    suspend fun getSearchPhotos(query: String, perPage: Int): Resource<List<ItemPhoto>>
}
