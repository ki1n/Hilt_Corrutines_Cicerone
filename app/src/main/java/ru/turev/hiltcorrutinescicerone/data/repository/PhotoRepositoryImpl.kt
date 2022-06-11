package ru.turev.hiltcorrutinescicerone.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import ru.turev.hiltcorrutinescicerone.data.network.api.ImageDownloadApi
import ru.turev.hiltcorrutinescicerone.data.network.api.PhotosDownloadApi
import ru.turev.hiltcorrutinescicerone.data.network.mapper.PhotoResponseMapper
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource
import ru.turev.hiltcorrutinescicerone.domain.errors.ResourceHandler
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photosDownloadApi: PhotosDownloadApi,
    private val photoResponseMapper: PhotoResponseMapper,
    private val imageDownloadApi: ImageDownloadApi
) : PhotoRepository, ResourceHandler {

    override suspend fun getAllPhotos(perPage: Int): Resource<List<ItemPhoto>> {
        return resource { photosDownloadApi.getAllPhotos(1, perPage).map { photoResponseMapper.mapToDomain(it) } }
    }

    override suspend fun getSearchPhotos(query: String, perPage: Int): Resource<List<ItemPhoto>> {
        return resource {
            photosDownloadApi.getSearchPhotos(query, perPage).images.map { photoResponseMapper.mapToDomain(it) }
        }
    }

    override suspend fun getBitmapFull(urlFull: String): ResponseBody {
        return withContext(Dispatchers.IO) {
            imageDownloadApi.getBitmapFull(urlFull)
        }
    }
}
