package ru.turev.hiltcorrutinescicerone.ui.photo_gallery.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.turev.hiltcorrutinescicerone.databinding.ItemPhotoGalleryBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto

class PhotoGalleryViewHolder(
    private val binding: ItemPhotoGalleryBinding,
) : RecyclerView.ViewHolder(binding.root) {

    val item = binding.imgItemPhoto

    fun bind(itemPhoto: ItemPhoto) {
        with(binding) {
            Glide.with(imgItemPhoto.context)
                .load(itemPhoto.small)
                .into(imgItemPhoto)

            imgInput.text = itemPhoto.name
        }
    }
}
