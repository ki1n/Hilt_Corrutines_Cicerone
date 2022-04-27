package ru.turev.hiltcorrutinescicerone.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.turev.hiltcorrutinescicerone.data.network.api.ApiService
import ru.turev.hiltcorrutinescicerone.data.network.mapper.PhotoResponseMapper
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource
import ru.turev.hiltcorrutinescicerone.domain.errors.ResourceHandler
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val photoResponseMapper: PhotoResponseMapper
) : PhotoRepository, ResourceHandler {

    override suspend fun getAllPhotos(page: Int, perPage: Int): Resource<List<ItemPhoto>> {
        return resource {
            withContext(Dispatchers.IO) {
                val list = apiService.getAllPhotos(page, perPage)
                photoResponseMapper.mapToDomain(list)
            }
        }
    }

    override suspend fun getSearchPhotos(query: String, perPage: Int): Resource<List<ItemPhoto>> {
        return resource {
            withContext(Dispatchers.IO) {
                val value = apiService.getSearchPhotos(query, perPage)
                photoResponseMapper.mapToDomain(value.images)
            }
        }
    }
}

