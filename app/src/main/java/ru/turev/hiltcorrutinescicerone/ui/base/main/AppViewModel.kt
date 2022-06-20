package ru.turev.hiltcorrutinescicerone.ui.base.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.turev.hiltcorrutinescicerone.domain.errors.AuthResponse
import ru.turev.hiltcorrutinescicerone.domain.interacror.AuthInteractor
import ru.turev.hiltcorrutinescicerone.navigation.Screens
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val router: Router,
    private val authInteractor: AuthInteractor
) : ViewModel() {

    fun getAccessToken(code: String?) {
        if (code != null) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = authInteractor.getNewAccessToken(code)) {
                    is AuthResponse.Success -> {
                        router.navigateToExt(Screens.Profile)
                        handleCurrentScreen()
                    }
                    is AuthResponse.Error -> {
                        _authError.trigger(viewModelScope)
                        Log.d("Auth ", response.error)
                    }
                }
            }
        }
    }

    fun startNavigation() {
        router.newRootScreen(Screens.photoGalleryScreen())
    }
}
