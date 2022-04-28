package ru.turev.hiltcorrutinescicerone.ui.detail_photo_view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentDetailsPhotoGalleryViewBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.binding.viewBinding

@AndroidEntryPoint
class DetailPhotoGalleryViewFragment : BaseFragment(R.layout.fragment_details_photo_gallery_view) {

    private val viewModel: DetailPhotoGalleryViewViewModel by viewModels()

    private val binding by viewBinding(FragmentDetailsPhotoGalleryViewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val ARGUMENT_PAYLOAD = "payload"

        fun getInstance(itemPhoto: ItemPhoto) = DetailPhotoGalleryViewFragment().apply {
            arguments = bundleOf(ARGUMENT_PAYLOAD to itemPhoto)
        }
    }
}
