package ru.turev.hiltcorrutinescicerone.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.image_photo.ImagePhotoFragment
import ru.turev.hiltcorrutinescicerone.ui.photo_gallery.PhotoGalleryFragment

object Screens {

    fun photoGalleryScreen(): FragmentScreen = FragmentScreen {
        PhotoGalleryFragment.getInstance()
    }

    fun detailPhotoGalleryViewScreen(itemPhoto: ItemPhoto): FragmentScreen = FragmentScreen {
        ImagePhotoFragment.getInstance(itemPhoto)
    }
}
