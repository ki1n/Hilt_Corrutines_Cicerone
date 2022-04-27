package ru.turev.hiltcorrutinescicerone.ui.photo_gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentPhotoGalleryBinding
import ru.turev.hiltcorrutinescicerone.extension.showSnackbar
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.binding.viewBinding
import ru.turev.hiltcorrutinescicerone.ui.photo_gallery.adapter.PhotoGalleryAdapter

@AndroidEntryPoint
open class PhotoGalleryFragment : BaseFragment(R.layout.fragment_photo_gallery) {

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
        }
    }

    private fun initAdapter() {
        binding?.rvPhotos?.adapter = adapter
        adapter.onClickListener = { viewModel.onDetailPhotoGalleryScreen(it) }
    }

    companion object {
        fun getInstance() = PhotoGalleryFragment()
    }
}
