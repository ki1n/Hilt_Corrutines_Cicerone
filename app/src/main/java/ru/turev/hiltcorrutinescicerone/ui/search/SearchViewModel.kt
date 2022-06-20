package ru.turev.hiltcorrutinescicerone.ui.search

import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.turev.hiltcorrutinescicerone.domain.interacror.SearchInteractor
import ru.turev.hiltcorrutinescicerone.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val router: Router,
    private val searchInteractor: SearchInteractor
) : BaseViewModel(router) {

    fun onSearchInputUpdate(searchInput: String) {
        viewModelScope.launch {
            searchInteractor.searchFlow.emit(searchInput)
        }
    }
}
