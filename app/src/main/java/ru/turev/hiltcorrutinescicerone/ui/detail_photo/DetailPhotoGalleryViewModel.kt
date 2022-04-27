package ru.turev.hiltcorrutinescicerone.ui.detail_photo

import androidx.lifecycle.LiveData
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import ru.turev.hiltcorrutinescicerone.util.renderscript.LiveEvent
import javax.inject.Inject

@HiltViewModel
class DetailPhotoGalleryViewModel @Inject constructor(
    private val router: Router
) : BaseViewModel(router) {

    val itemPhoto: LiveData<ItemPhoto> get() = _itemPhoto
    private val _itemPhoto = LiveEvent<ItemPhoto>()

    fun setData(itemPhoto: ItemPhoto) {
        _itemPhoto.value = itemPhoto
    }
}
