package ru.turev.hiltcorrutinescicerone.ui.photoGallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhotoPage
import ru.turev.hiltcorrutinescicerone.domain.errors.Resource
import ru.turev.hiltcorrutinescicerone.domain.interacror.SearchInteractor
import ru.turev.hiltcorrutinescicerone.domain.repository.PhotoRepository
import ru.turev.hiltcorrutinescicerone.navigation.Screens
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import ru.turev.hiltcorrutinescicerone.util.LiveEvent
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    private val router: Router,
    private val photoRepository: PhotoRepository,
    private val searchInteractor: SearchInteractor
) : BaseViewModel(router) {

    data class Model(
        val items: List<ItemPhoto> = emptyList(),
        val page: Int = 1,
        val pageCount: Int = 1
    )

    private var uiState = Model()
        set(value) {
            _photos.value = value
            field = value
        }

    val showLoadError: LiveData<Unit> get() = _showLoadError
    private val _showLoadError = LiveEvent<Unit>()

    val showLoadErrorNetwork: LiveData<Unit> get() = _showLoadErrorNetwork
    private val _showLoadErrorNetwork = LiveEvent<Unit>()

    val photos: LiveData<Model> get() = _photos
    private val _photos = MutableLiveData<Model>()

    val searchFlow: SharedFlow<String> = searchInteractor.searchFlow.asSharedFlow()

    init {
        getAllPhotos()
    }

    private fun setPhotos(itemsPhotoPage: ItemPhotoPage) {
        uiState = uiState.copy(
            items = uiState.items + itemsPhotoPage.items,
            page = itemsPhotoPage.page,
            pageCount = itemsPhotoPage.pageCount
        )
    }

    fun onDetailPhotoGalleryViewScreen(itemPhoto: ItemPhoto) =
        router.navigateTo(Screens.detailPhotoGalleryViewScreen(itemPhoto))

    fun onSearchScreen() = router.navigateTo(Screens.searchScreen())

    private fun getAllPhotos(next: Boolean = false) {
        viewModelScope.launch(Dispatchers.Main) {
            val result: Resource<ItemPhotoPage> = if (next) {
                photoRepository.getAllPhotos(uiState.page.inc())
            } else {
                photoRepository.getAllPhotos(uiState.page)
            }

            when (result) {
                is Resource.NetworkError -> _showLoadErrorNetwork.call()
                is Resource.Error -> _showLoadError.call()
                is Resource.Success -> setPhotos(result.data)
            }
        }
    }

    fun loadNextPage() {
        getAllPhotos(true)
    }
}
