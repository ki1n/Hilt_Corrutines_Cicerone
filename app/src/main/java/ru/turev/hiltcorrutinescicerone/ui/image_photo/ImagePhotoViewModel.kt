package ru.turev.hiltcorrutinescicerone.ui.image_photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import ru.turev.hiltcorrutinescicerone.util.renderscript.LiveEvent
import javax.inject.Inject

@HiltViewModel
class ImagePhotoViewModel @Inject constructor(
    private val router: Router
) : BaseViewModel(router) {

    val isDraw: LiveData<Boolean> get() = _isDraw
    private val _isDraw = MediatorLiveData<Boolean>()

    val showDraw: LiveData<Unit> get() = _showDraw
    private val _showDraw = LiveEvent<Unit>()

    val showExitDraw: LiveData<Unit> get() = _showExitDraw
    private val _showExitDraw = LiveEvent<Unit>()

    val isClearDraw: LiveData<Boolean> get() = _isClearDraw
    private val _isClearDraw = MediatorLiveData<Boolean>()

    val showClearDraw: LiveData<Unit> get() = _showClearDraw
    private val _showClearDraw = LiveEvent<Unit>()

    val isSave: LiveData<Boolean> get() = _isSave
    private val _isSave = MediatorLiveData<Boolean>()

    val showSave: LiveData<Unit> get() = _showSave
    private val _showSave = LiveEvent<Unit>()

    fun onDraw() {
        _isDraw.value = true
        _showDraw.call()
    }

    fun onExitDraw() {
        _isDraw.value = false
        _showExitDraw.call()
    }

    fun onClearDraw() {
        _isClearDraw.value = true
        _showClearDraw.call()
    }

    fun onSaveImage() {
        _showSave.call()
    }
}
