package ru.turev.hiltcorrutinescicerone.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.turev.hiltcorrutinescicerone.ui.main.PhotoGalleryFragment

object Screens {

    fun main(): FragmentScreen = FragmentScreen { PhotoGalleryFragment() }

}
