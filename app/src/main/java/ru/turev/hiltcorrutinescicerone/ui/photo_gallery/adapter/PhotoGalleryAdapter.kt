package ru.turev.hiltcorrutinescicerone.ui.photo_gallery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.turev.hiltcorrutinescicerone.databinding.ItemPhotoGalleryBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.util.setOnDebouncedClickListener

class PhotoGalleryAdapter :
        ListAdapter<ItemPhoto, PhotoGalleryViewHolder>(PhotoGalleryDiffCallback()) {

    var onClickListener: ((ItemPhoto) -> Unit) = {}
    // lateinit var onClickListener: ((ItemPhoto, Unit) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGalleryViewHolder {
        val binding =
            ItemPhotoGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoGalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoGalleryViewHolder, position: Int) {
        val currentPhoto = currentList[position]
        holder.bind(currentPhoto)
        holder.item.setOnDebouncedClickListener {
            onClickListener.invoke(currentPhoto)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}
