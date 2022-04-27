package ru.turev.hiltcorrutinescicerone.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.detail_photo.DetailPhotoGalleryFragment
import ru.turev.hiltcorrutinescicerone.ui.photo_gallery.PhotoGalleryFragment

object Screens {

    fun detailPhotoGalleryScreen(itemPhoto: ItemPhoto): FragmentScreen = FragmentScreen {
        DetailPhotoGalleryFragment.getInstance(itemPhoto)
    }

    fun photoGalleryScreen(): FragmentScreen = FragmentScreen {
        PhotoGalleryFragment.getInstance()
    }
}
