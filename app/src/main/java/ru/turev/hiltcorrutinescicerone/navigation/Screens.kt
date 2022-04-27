package ru.turev.hiltcorrutinescicerone.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.turev.hiltcorrutinescicerone.ui.main.MainFragment

object Screens {

    fun main(): FragmentScreen = FragmentScreen { MainFragment() }

}
