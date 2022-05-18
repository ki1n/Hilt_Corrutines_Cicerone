package ru.turev.hiltcorrutinescicerone.data.network.mapper

import ru.turev.hiltcorrutinescicerone.data.network.api.response.PhotoResponse
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto

class PhotoResponseMapper {

    fun mapToDomain(response: PhotoResponse): ItemPhoto {
        return ItemPhoto(
            id = response.id,
            raw = response.urls.raw,
            full = response.urls.full,
            regular = response.urls.regular,
            small = response.urls.small,
            thumb = response.urls.thumb,
            smallS3 = response.urls.smallS3,
            likes = response.likes,
            name = response.user.name
        )
    }
}
