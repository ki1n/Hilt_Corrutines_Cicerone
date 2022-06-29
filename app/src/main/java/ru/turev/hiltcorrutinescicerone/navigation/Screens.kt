package ru.turev.hiltcorrutinescicerone.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.imagePhoto.ImagePhotoFragment
import ru.turev.hiltcorrutinescicerone.ui.photoGallery.PhotoGalleryFragment
import ru.turev.hiltcorrutinescicerone.ui.search.SearchFragment

object Screens {

    fun photoGalleryScreen(): FragmentScreen = FragmentScreen {
        PhotoGalleryFragment.getInstance()
    }

    fun detailPhotoGalleryViewScreen(itemPhoto: ItemPhoto): FragmentScreen = FragmentScreen {
        ImagePhotoFragment.getInstance(itemPhoto)
    }

    fun searchScreen(): FragmentScreen = FragmentScreen {
        SearchFragment.getInstance()
    }
}
