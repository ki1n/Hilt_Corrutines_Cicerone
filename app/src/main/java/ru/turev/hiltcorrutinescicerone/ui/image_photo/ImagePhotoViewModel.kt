package ru.turev.hiltcorrutinescicerone.ui.image_photo

import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ImagePhotoViewModel @Inject constructor(
    private val router: Router
) : BaseViewModel(router) {
}
