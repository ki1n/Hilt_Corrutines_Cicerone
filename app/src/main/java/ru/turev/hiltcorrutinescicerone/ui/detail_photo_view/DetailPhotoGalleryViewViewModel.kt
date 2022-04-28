package ru.turev.hiltcorrutinescicerone.ui.detail_photo_view

import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DetailPhotoGalleryViewViewModel @Inject constructor(
    private val router: Router
) : BaseViewModel(router) {
}
