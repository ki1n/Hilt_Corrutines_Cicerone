package ru.turev.hiltcorrutinescicerone.ui.photo_gallery

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentPhotoGalleryBinding
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.binding.viewBinding
import ru.turev.hiltcorrutinescicerone.ui.photo_gallery.adapter.PhotoGalleryAdapter
import ru.turev.hiltcorrutinescicerone.util.constants.Constants
import ru.turev.hiltcorrutinescicerone.util.extension.showSnackbar

@AndroidEntryPoint
open class PhotoGalleryFragment : BaseFragment(R.layout.fragment_photo_gallery) {

    companion object {
        fun getInstance() = PhotoGalleryFragment()
    }

    private val viewModel: PhotoGalleryViewModel by viewModels()

    private val binding by viewBinding(FragmentPhotoGalleryBinding::bind)

    private val adapter: PhotoGalleryAdapter by lazy { PhotoGalleryAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        viewModel.run {
            photos.observe { adapter.submitList(it) }
            showLoadError.observe { showSnackbar(R.string.photo_error) }
            showLoadErrorNetwork.observe { showSnackbar(R.string.photo_error_network) }
            searchInput.observe(viewModel::onSearchInputUpdate)
            isSearchInputEmpty.observe(::onSubscribedSearchInputEmpty)
        }
        with(binding) {
            appBarPhotoGallerySearch.etSearch.doAfterTextChanged { searchInput ->
                viewModel.onSearchInputUpdate(searchInput.toString())
            }
            appBarPhotoGallerySearch.imgSearch.setOnClickListener {
                viewModel.onSearch()
            }

            appBarPhotoGallerySearch.imgClear.setOnClickListener {
                viewModel.onClear()
                appBarPhotoGallerySearch.etSearch.setText(Constants.STRING_EMPTY)
            }
        }
    }

    private fun initAdapter() {
        binding.rvPhotos.adapter = adapter
        adapter.onClickListener = { itemPhoto -> viewModel.onDetailPhotoGalleryViewScreen(itemPhoto) }
    }

    private fun onSubscribedSearchInputEmpty(isSearchInputEmpty: Boolean) {
        with(binding) {
            appBarPhotoGallerySearch.imgClear.isVisible = isSearchInputEmpty
            appBarPhotoGallerySearch.imgSearch.isVisible = !isSearchInputEmpty
        }
    }
}
