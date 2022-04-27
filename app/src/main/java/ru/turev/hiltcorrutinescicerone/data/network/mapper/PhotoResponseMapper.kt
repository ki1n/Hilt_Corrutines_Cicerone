package ru.turev.hiltcorrutinescicerone.data.network.mapper

import ru.turev.hiltcorrutinescicerone.data.network.api.response.PhotoResponse
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto

class PhotoResponseMapper {

    fun mapToDomain(response: List<PhotoResponse>): List<ItemPhoto> {
        val result = mutableListOf<ItemPhoto>()
        for (element in response) {
            val itemPhoto = ItemPhoto(
                id = element.id,
                raw = element.urls.raw,
                full = element.urls.full,
                regular = element.urls.regular,
                small = element.urls.small,
                thumb = element.urls.thumb,
                smallS3 = element.urls.smallS3,
                likes = element.likes,
                name = element.user.name
            )
            result.add(itemPhoto)
        }
        return result
    }
}
