package ru.turev.hiltcorrutinescicerone.ui.detail_photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.databinding.FragmentDetailsPhotoBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment

@AndroidEntryPoint
class DetailPhotoGalleryFragment : BaseFragment<FragmentDetailsPhotoBinding>() {

    private val viewModel: DetailPhotoGalleryViewModel by viewModels()

    override fun getBinding(container: ViewGroup?) = FragmentDetailsPhotoBinding.inflate(layoutInflater)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    companion object {
        private const val ARGUMENT_PAYLOAD = "payload"

        fun getInstance(itemPhoto: ItemPhoto) = DetailPhotoGalleryFragment().apply {
            arguments = bundleOf(ARGUMENT_PAYLOAD to itemPhoto)
        }
    }
}
