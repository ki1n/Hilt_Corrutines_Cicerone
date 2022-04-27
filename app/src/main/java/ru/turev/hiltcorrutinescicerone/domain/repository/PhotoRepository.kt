package ru.turev.hiltcorrutinescicerone.domain.repository

import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto

interface PhotoRepository {

    suspend fun getAllPhotos(page: Int, perPage: Int): List<ItemPhoto>

    suspend fun getSearchPhotos(query: String, perPage: Int): List<ItemPhoto>
}
