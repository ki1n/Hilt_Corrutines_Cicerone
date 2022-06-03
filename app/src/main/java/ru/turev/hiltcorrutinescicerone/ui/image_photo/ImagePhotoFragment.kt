package ru.turev.hiltcorrutinescicerone.ui.image_photo

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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

    companion object {
        private const val ARGUMENT_PAYLOAD = "payload"

        fun getInstance(itemPhoto: ItemPhoto) = ImagePhotoFragment().apply {
            arguments = bundleOf(ARGUMENT_PAYLOAD to itemPhoto)
        }
    }

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
            showSave.observe { showSnackbar(R.string.image_photo_show_save) }
        }
        with(binding) {
            appBarImagePhoto.imgBack.setOnClickListener { viewModel.onExit() }
            appBarImagePhoto.tvName.text = itemPhoto.name
            appBarImagePhoto.imgDraw.setOnClickListener { viewModel.onDraw() }
            appBarImagePhoto.imgExitDraw.setOnClickListener { viewModel.onExitDraw() }
            appBarImagePhoto.imgClear.setOnClickListener { viewModel.onClearDraw() }
            appBarImagePhoto.imgSave.setOnClickListener { saveImage() }
        }
    }

    private fun onSubscribedClearDraw(isClearDraw: Boolean) {
        binding.imagePhotoView.setIsClearPatch(isClearDraw)
    }

    private fun onSubscribedDrawOnClear(isDraw: Boolean) {
        with(binding) {
            imagePhotoView.setIsDrawMode(isDraw)
            appBarImagePhoto.imgDraw.isVisible = !isDraw
            appBarImagePhoto.imgExitDraw.isVisible = isDraw
            appBarImagePhoto.imgClear.isVisible = isDraw
        }
    }

    private fun initData() {
        lifecycleScope.launch {
            whenStarted {
                with(binding) {
                    val placeholder = getPlaceholder()
                    val bitmapFull = getBitmapFull()
                    imagePhotoView.setBitmapFull(true, bitmapFull)
                    getImagePhotoFull(imagePhotoView, placeholder)
                }
            }
        }
    }

    private fun saveImage() {
        viewModel.onSaveImage()
        binding.imagePhotoView.saveImage()
    }

    private fun getImagePhotoFull(imagePhotoView: ImagePhotoView, placeholder: Drawable) {
        val url = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) itemPhoto.full else itemPhoto.regular
        Glide.with(requireContext())
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(
                    p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean
                ): Boolean {
                    imagePhotoView.setAllowZoomImage(true)
                    return false
                }
            })
            .placeholder(placeholder)
            .into(imagePhotoView)
    }

    private suspend fun getPlaceholder(): Drawable =
        withContext(Dispatchers.IO) { Glide.with(requireContext()).asDrawable().load(itemPhoto.small).submit().get() }

    private suspend fun getBitmapFull(): Bitmap = withContext(Dispatchers.IO) {
        val url = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) itemPhoto.full else itemPhoto.regular
        Glide.with(binding.imagePhotoView.context).asBitmap().load(url).submit().get()
    }
}
