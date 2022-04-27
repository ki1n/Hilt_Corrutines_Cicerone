package ru.turev.hiltcorrutinescicerone.ui.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import ru.turev.hiltcorrutinescicerone.navigation.Screens
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    private val router: Router,
    private val photoRepository: PhotoRepository
) : BaseViewModel(router) {

    val photos: LiveData<List<ItemPhoto>> get() = _photos
    private val _photos = MutableLiveData<List<ItemPhoto>>(listOf())








    fun onDetailPhotoGalleryScreen(itemPhoto: ItemPhoto) =
        router.navigateTo(Screens.detailPhotoGalleryScreen(itemPhoto))
}
