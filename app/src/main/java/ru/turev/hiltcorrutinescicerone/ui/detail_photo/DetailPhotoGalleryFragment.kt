package ru.turev.hiltcorrutinescicerone.ui.detail_photo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentDetailsPhotoBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.binding.viewBinding

@AndroidEntryPoint
class DetailPhotoGalleryFragment : BaseFragment(R.layout.fragment_details_photo) {

    private val viewModel: DetailPhotoGalleryViewModel by viewModels()

    private val binding by viewBinding(FragmentDetailsPhotoBinding::bind)

    private val itemPhoto by lazy { requireArguments().getParcelable<ItemPhoto>(ARGUMENT_PAYLOAD)!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        viewModel.run { itemPhoto.observe(::onDraw) }
        binding.appBarPhotoGallerySearch.imgBack.setOnClickListener {
            viewModel.onExit()
        }
    }

    private fun initData() {
        viewModel.setData(itemPhoto)
    }

    private fun onDraw(itemPhoto: ItemPhoto) {
        with(binding) {
            Glide.with(imageFull.context)
                .load(itemPhoto.full)
                .into(imageFull)
            appBarPhotoGallerySearch.tvName.text = itemPhoto.name
        }
    }

    companion object {
        private const val ARGUMENT_PAYLOAD = "payload"

        fun getInstance(itemPhoto: ItemPhoto) = DetailPhotoGalleryFragment().apply {
            arguments = bundleOf(ARGUMENT_PAYLOAD to itemPhoto)
        }
    }
}
