package ru.turev.hiltcorrutinescicerone.ui.photo_gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import ru.turev.hiltcorrutinescicerone.navigation.Screens
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import ru.turev.hiltcorrutinescicerone.util.LiveEvent
import ru.turev.hiltcorrutinescicerone.util.constants.Constants
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

    val searchInput: LiveData<String> get() = _searchInput
    private val _searchInput = MutableLiveData<String>()

    val isSearchInputEmpty: LiveData<Boolean> get() = _isSearchInputEmpty
    private val _isSearchInputEmpty = MediatorLiveData<Boolean>()

    companion object {
        private const val STANDARD_QUANTITY = 20
        // private const val INITIAL_VALUE = 1
    }

    init {
        getAllPhotos()
    }

    private fun setPhotos(photos: List<ItemPhoto>) {
        _photos.postValue(photos)
    }

    fun onDetailPhotoGalleryViewScreen(itemPhoto: ItemPhoto) =
        router.navigateTo(Screens.detailPhotoGalleryViewScreen(itemPhoto))

    fun onSearchInputUpdate(searchInput: String) {
        _searchInput.postValue(searchInput)
        _isSearchInputEmpty.postValue(false)
    }

    fun onClear() {
        _searchInput.postValue(Constants.STRING_EMPTY)
        _isSearchInputEmpty.postValue(true)
    }

    fun onSearch() = getSearchPhotos(searchInput.value.toString())

    private fun getAllPhotos() {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result = photoRepository.getAllPhotos(STANDARD_QUANTITY)) {
                is Resource.NetworkError -> _showLoadErrorNetwork.call()
                is Resource.Error -> _showLoadError.call()
                is Resource.Success -> setPhotos(result.data)
            }
        }
    }

    private fun getSearchPhotos(searchInput: String) {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result = photoRepository.getSearchPhotos(searchInput, STANDARD_QUANTITY)) {
                is Resource.NetworkError -> _showLoadErrorNetwork.call()
                is Resource.Error -> _showLoadError.call()
                is Resource.Success -> setPhotos(result.data)
            }
        }
    }
}
