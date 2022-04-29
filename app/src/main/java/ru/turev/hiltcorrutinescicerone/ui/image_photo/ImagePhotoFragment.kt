package ru.turev.hiltcorrutinescicerone.ui.image_photo

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentImagePhotoBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.binding.viewBinding

@AndroidEntryPoint
class ImagePhotoFragment : BaseFragment(R.layout.fragment_image_photo) {

    private val viewModel: ImagePhotoViewModel by viewModels()

    private val binding by viewBinding(FragmentImagePhotoBinding::bind)

    private val itemPhoto by lazy { requireArguments().getParcelable<ItemPhoto>(ARGUMENT_PAYLOAD)!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        with(binding) {
            appBarImagePhoto.imgBack.setOnClickListener { viewModel.onExit() }
            appBarImagePhoto.tvName.text = itemPhoto.name
        }
    }

    private fun initData() {
        with(binding) {
            Glide.with(imagePhotoView.context)
                .asBitmap()
                .load(itemPhoto.full)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imagePhotoView.setData(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        }
    }

    companion object {
        private const val ARGUMENT_PAYLOAD = "payload"

        fun getInstance(itemPhoto: ItemPhoto) = ImagePhotoFragment().apply {
            arguments = bundleOf(ARGUMENT_PAYLOAD to itemPhoto)
        }
    }
}
