package ru.turev.hiltcorrutinescicerone.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import ru.turev.hiltcorrutinescicerone.data.network.api.ImageDownloadApi
import ru.turev.hiltcorrutinescicerone.data.network.api.PhotosDownloadApi
import ru.turev.hiltcorrutinescicerone.data.network.mapper.PhotoResponseMapper
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhotoPage
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource
import ru.turev.hiltcorrutinescicerone.domain.errors.ResourceHandler
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import ru.turev.hiltcorrutinescicerone.util.constants.Const
import javax.inject.Inject
import kotlin.math.ceil

class PhotoRepositoryImpl @Inject constructor(
    private val photosDownloadApi: PhotosDownloadApi,
    private val photoResponseMapper: PhotoResponseMapper,
    private val imageDownloadApi: ImageDownloadApi
) : PhotoRepository, ResourceHandler {

    override suspend fun getAllPhotos(page: Int): Resource<ItemPhotoPage> {
        return resource {
            ItemPhotoPage(
                items = photosDownloadApi.getAllPhotos(page, Const.STANDARD_REQUEST_IMAGES)
                    .map { photoResponseMapper.mapToDomain(it) },
                page = page,
                pageCount = ceil(4.toDouble() / Const.STANDARD_REQUEST_IMAGES).toInt()
            )
        }
    }

    override suspend fun getSearchPhotos(query: String): Resource<List<ItemPhoto>> {
        return resource {
            photosDownloadApi.getSearchPhotos(
                query,
                Const.STANDARD_REQUEST_IMAGES
            ).images.map { photoResponseMapper.mapToDomain(it) }
        }
    }

    override suspend fun getBitmapFull(urlFull: String): ResponseBody {
        return withContext(Dispatchers.IO) {
            imageDownloadApi.getBitmapFull(urlFull)
        }
    }
}
