package ru.turev.hiltcorrutinescicerone.domain.entity

data class ItemPhotoPage(
    val items: List<ItemPhoto> = emptyList(),
    val page: Int = 1,
    val pageCount: Int = 1
)
