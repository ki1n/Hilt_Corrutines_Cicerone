package ru.turev.hiltcorrutinescicerone.ui.photoGallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentPhotoGalleryBinding
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.recyclerview.BaseAdapter
import ru.turev.hiltcorrutinescicerone.ui.base.recyclerview.LoadMoreScrollListener
import ru.turev.hiltcorrutinescicerone.ui.photoGallery.delegates.photosAdapterDelegate
import ru.turev.hiltcorrutinescicerone.util.extension.launchWhenStarted
import ru.turev.hiltcorrutinescicerone.util.extension.showSnackbar

@AndroidEntryPoint
class PhotoGalleryFragment : BaseFragment(R.layout.fragment_photo_gallery) {

    companion object {
        fun getInstance() = PhotoGalleryFragment()
    }

    private val viewModel: PhotoGalleryViewModel by viewModels()

//  private val binding by viewBinding(FragmentPhotoGalleryBinding::bind)

    // todo новый биндинг от Кирила Розова
    private val binding by viewBinding(FragmentPhotoGalleryBinding::bind)

    private val adapter by lazy {
        BaseAdapter(
            photosAdapterDelegate(
                onItemClick = { itemPhoto -> viewModel.onDetailPhotoGalleryViewScreen(itemPhoto) }
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        viewModel.searchFlow.onEach {
            handleStateSearch(it)
        }.launchWhenStarted(lifecycleScope)

        viewModel.run {
            photos.observe(::handleStatePhotos)
            showLoadError.observe { showSnackbar(R.string.photo_error) }
            showLoadErrorNetwork.observe { showSnackbar(R.string.photo_error_network) }
        }
        with(binding) {
            appBarPhotoGallery.imgInput.setOnClickListener { viewModel.onSearchScreen() }
            appBarPhotoGallery.imgSearch.setOnClickListener { viewModel.onSearchScreen() }
        }
    }

    private fun initAdapter() {
        with(binding) {
            rvPhotos.adapter = adapter
            rvPhotos.addOnScrollListener(
                LoadMoreScrollListener {
                    viewModel.loadNextPage()
                }
            )
        }
    }

    private fun handleStateSearch(string: String) {
        binding.tvSearchFlow.text = string
    }

    private fun handleStatePhotos(state: PhotoGalleryViewModel.Model) {
        adapter.items = state.items
    }
}
