package ru.turev.hiltcorrutinescicerone.ui.base

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router

abstract class BaseViewModel(
    private val router: Router,
) : ViewModel() {

    open fun onExit() {
        router.exit()
    }
}
