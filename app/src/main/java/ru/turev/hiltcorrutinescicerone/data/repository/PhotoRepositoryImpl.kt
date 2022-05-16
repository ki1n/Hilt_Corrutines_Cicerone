package ru.turev.hiltcorrutinescicerone.data.repository

import ru.turev.hiltcorrutinescicerone.data.network.api.PhotosDownloadApi
import ru.turev.hiltcorrutinescicerone.data.network.mapper.PhotoResponseMapper
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource
import ru.turev.hiltcorrutinescicerone.domain.errors.ResourceHandler
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photosDownloadApi: PhotosDownloadApi,
    private val photoResponseMapper: PhotoResponseMapper
) : PhotoRepository, ResourceHandler {

    override suspend fun getAllPhotos(perPage: Int): Resource<List<ItemPhoto>> {
        return resource { photosDownloadApi.getAllPhotos(1, perPage).map { photoResponseMapper.mapToDomain(it) } }
    }

    override suspend fun getSearchPhotos(query: String, perPage: Int): Resource<List<ItemPhoto>> {
        return resource {
            photosDownloadApi.getSearchPhotos(query, perPage).images.map { photoResponseMapper.mapToDomain(it) }
        }
    }
}

