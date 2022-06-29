package ru.turev.hiltcorrutinescicerone.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentSearchBinding
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.binding.viewBinding
import ru.turev.hiltcorrutinescicerone.ui.search.adapter.PhotoGallerySearchAdapter
import ru.turev.hiltcorrutinescicerone.util.constants.Const
import ru.turev.hiltcorrutinescicerone.util.extension.showSnackbar

@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.fragment_search) {

    companion object {
        fun getInstance() = SearchFragment()
    }

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val viewModel: SearchViewModel by viewModels()

    private val adapter: PhotoGallerySearchAdapter by lazy { PhotoGallerySearchAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        viewModel.run {
            photosSearch.observe { adapter.submitList(it) }
            showLoadError.observe { showSnackbar(R.string.photo_error) }
            showLoadErrorNetwork.observe { showSnackbar(R.string.photo_error_network) }
            searchInput.observe(viewModel::onSearchInputUpdate)
            isSearchInputEmpty.observe(::onSubscribedSearchInputEmpty)
        }

        with(binding) {
            etSaveText.doAfterTextChanged { searchInput ->
                viewModel.onSearchInputUpdateInteractor(searchInput.toString())
            }

            appBarPhotoGallerySearch.etSearch.doAfterTextChanged { searchInput ->
                viewModel.onSearchInputUpdate(searchInput.toString())
            }

            appBarPhotoGallerySearch.imgSearch.setOnClickListener { viewModel.onSearch() }

            appBarPhotoGallerySearch.imgClear.setOnClickListener {
                viewModel.onClear()
                appBarPhotoGallerySearch.etSearch.setText(Const.STRING_EMPTY)
            }
        }
    }

    private fun initAdapter() {
        with(binding) {
            rvPhotosSearch.adapter = adapter
            adapter.onClickListener = { itemPhoto -> viewModel.onDetailPhotoGalleryViewScreen(itemPhoto) }
        }
    }

    private fun onSubscribedSearchInputEmpty(isSearchInputEmpty: Boolean) {
        with(binding) {
            appBarPhotoGallerySearch.imgClear.isVisible = isSearchInputEmpty
            appBarPhotoGallerySearch.imgSearch.isVisible = !isSearchInputEmpty
        }
    }
}
