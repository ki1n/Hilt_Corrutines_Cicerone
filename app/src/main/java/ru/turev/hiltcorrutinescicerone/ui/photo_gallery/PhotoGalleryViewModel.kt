package ru.turev.hiltcorrutinescicerone.ui.photo_gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import ru.turev.hiltcorrutinescicerone.navigation.Screens
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import ru.turev.hiltcorrutinescicerone.util.renderscript.LiveEvent
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    private val router: Router,
    private val photoRepository: PhotoRepository
) : BaseViewModel(router) {

    val showLoadError: LiveData<Unit> get() = _showLoadError
    private val _showLoadError = LiveEvent<Unit>()

    val showLoadErrorNetwork: LiveData<Unit> get() = _showLoadErrorNetwork
    private val _showLoadErrorNetwork = LiveEvent<Unit>()

    val photos: LiveData<List<ItemPhoto>> get() = _photos
    private val _photos = MutableLiveData<List<ItemPhoto>>(listOf())

    private fun getAllPhotos() {
        viewModelScope.launch {
            when (val result = photoRepository.getAllPhotos(INITIAL_VALUE, STANDARD_QUANTITY)) {
                is Resource.Error -> _showLoadError.call()
                is Resource.Success -> setPhotos(result.data)
                is Resource.NetworkError -> _showLoadErrorNetwork.call()
            }
        }
    }

    private fun setPhotos(photos: List<ItemPhoto>) {
        _photos.value = photos
    }

    fun onDetailPhotoGalleryScreen(itemPhoto: ItemPhoto) =
        router.navigateTo(Screens.detailPhotoGalleryScreen(itemPhoto))

    companion object {
        private const val STANDARD_QUANTITY = 20
        private const val INITIAL_VALUE = 1
    }

    init {
        getAllPhotos()
    }
}
