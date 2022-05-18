package ru.turev.hiltcorrutinescicerone.ui.photo_gallery.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto

class PhotoGalleryDiffCallback : DiffUtil.ItemCallback<ItemPhoto>() {

    override fun areItemsTheSame(oldItem: ItemPhoto, newItem: ItemPhoto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ItemPhoto, newItem: ItemPhoto): Boolean {
        return oldItem == newItem
    }
}
