package ru.turev.hiltcorrutinescicerone.ui.base

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.turev.hiltcorrutinescicerone.navigation.Screens
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    fun startNavigation() {
        router.newRootScreen(Screens.main())
    }
}
