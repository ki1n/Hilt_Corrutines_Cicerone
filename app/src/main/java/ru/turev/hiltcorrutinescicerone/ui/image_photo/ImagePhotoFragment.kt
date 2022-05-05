package ru.turev.hiltcorrutinescicerone.ui.image_photo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentImagePhotoBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.binding.viewBinding
import ru.turev.hiltcorrutinescicerone.util.extension.showSnackbar
import ru.turev.hiltcorrutinescicerone.view.ImagePhotoView

@AndroidEntryPoint
class ImagePhotoFragment : BaseFragment(R.layout.fragment_image_photo) {

    private val viewModel: ImagePhotoViewModel by viewModels()

    private val binding by viewBinding(FragmentImagePhotoBinding::bind)

    private val itemPhoto by lazy { requireArguments().getParcelable<ItemPhoto>(ARGUMENT_PAYLOAD)!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        viewModel.run {
            isDraw.observe(::onSubscribedDrawOnClear)
            showDraw.observe { showSnackbar(R.string.image_photo_show_draw) }
            showExitDraw.observe { showSnackbar(R.string.image_photo_exit_show_draw) }
            isClearDraw.observe(::onSubscribedClearDraw)
            showClearDraw.observe { showSnackbar(R.string.image_photo_show_clear_draw) }
        }
        with(binding) {
            appBarImagePhoto.imgBack.setOnClickListener { viewModel.onExit() }
            appBarImagePhoto.tvName.text = itemPhoto.name
            appBarImagePhoto.imgDraw.setOnClickListener { viewModel.onDraw() }
            appBarImagePhoto.imgExitDraw.setOnClickListener { viewModel.onExitDraw() }
            appBarImagePhoto.imgClear.setOnClickListener { viewModel.onClearDraw() }
        }
    }

    private fun onSubscribedClearDraw(isClearDraw: Boolean) {
        binding.imagePhotoView.setIsClearPatch(isClearDraw)
    }

    private fun onSubscribedDrawOnClear(isDraw: Boolean) {
        with(binding) {
            imagePhotoView.setIsDrawMode(isDraw)
            if (isDraw) {
                appBarImagePhoto.imgDraw.isVisible = false
                appBarImagePhoto.imgExitDraw.isVisible = true
                appBarImagePhoto.imgClear.isVisible = true
            } else {
                appBarImagePhoto.imgDraw.isVisible = true
                appBarImagePhoto.imgExitDraw.isVisible = false
                appBarImagePhoto.imgClear.isVisible = false
            }
        }
    }

    private fun initData() {
        lifecycleScope.launch {
            whenStarted {
                binding.imagePhotoView.let { imagePhotoView ->
                    val placeholder = getPlaceholder()
                    getImagePhotoFull(imagePhotoView)
                }
            }
        }
    }

    private fun getImagePhotoFull(imagePhotoView: ImagePhotoView) {
        Glide.with(requireContext())
            .load(itemPhoto.full)
            //.apply(RequestOptions.overrideOf(800,600))
            .transition(DrawableTransitionOptions.withCrossFade())
            // .placeholder(placeholder)
            .into(imagePhotoView)
    }

    private suspend fun getPlaceholder() = withContext(Dispatchers.IO) {
        return@withContext Glide.with(requireContext()).asDrawable().load(itemPhoto.small).submit().get()
    }

    companion object {
        private const val ARGUMENT_PAYLOAD = "payload"

        fun getInstance(itemPhoto: ItemPhoto) = ImagePhotoFragment().apply {
            arguments = bundleOf(ARGUMENT_PAYLOAD to itemPhoto)
        }
    }
}
