package ru.turev.hiltcorrutinescicerone.ui.search

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
import ru.turev.hiltcorrutinescicerone.domain.interacror.SearchInteractor
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import ru.turev.hiltcorrutinescicerone.navigation.Screens
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import ru.turev.hiltcorrutinescicerone.util.LiveEvent
import ru.turev.hiltcorrutinescicerone.util.constants.Const
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val router: Router,
    private val photoRepository: PhotoRepository,
    private val searchInteractor: SearchInteractor
) : BaseViewModel(router) {

    val showLoadError: LiveData<Unit> get() = _showLoadError
    private val _showLoadError = LiveEvent<Unit>()

    val showLoadErrorNetwork: LiveData<Unit> get() = _showLoadErrorNetwork
    private val _showLoadErrorNetwork = LiveEvent<Unit>()

    val photosSearch: LiveData<List<ItemPhoto>> get() = _photosSearch
    private val _photosSearch = MutableLiveData<List<ItemPhoto>>(listOf())

    val searchInput: LiveData<String> get() = _searchInput
    private val _searchInput = MutableLiveData<String>()

    val isSearchInputEmpty: LiveData<Boolean> get() = _isSearchInputEmpty
    private val _isSearchInputEmpty = MediatorLiveData<Boolean>()

    private fun setPhotosSearch(list: List<ItemPhoto>) {
        _photosSearch.value = list
    }

    fun onSearchInputUpdate(searchInput: String) {
        _searchInput.postValue(searchInput)
        _isSearchInputEmpty.postValue(false)
    }

    fun onClear() {
        _searchInput.postValue(Const.STRING_EMPTY)
        _isSearchInputEmpty.postValue(true)
    }

    fun onSearch() = getSearchPhotos(searchInput.value.toString())

    private fun getSearchPhotos(searchInput: String) {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result = photoRepository.getSearchPhotos(searchInput)) {
                is Resource.NetworkError -> _showLoadErrorNetwork.call()
                is Resource.Error -> _showLoadError.call()
                is Resource.Success -> {
                    setPhotosSearch(result.data)
                }
            }
        }
    }

    fun onSearchInputUpdateInteractor(searchInput: String) {
        viewModelScope.launch {
            searchInteractor.searchFlow.emit(searchInput)
        }
    }

    fun onDetailPhotoGalleryViewScreen(itemPhoto: ItemPhoto) =
        router.navigateTo(Screens.detailPhotoGalleryViewScreen(itemPhoto))
}
