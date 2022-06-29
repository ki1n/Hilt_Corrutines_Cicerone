package ru.turev.hiltcorrutinescicerone.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.turev.hiltcorrutinescicerone.databinding.ItemPhotoGalleryBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto
import ru.turev.hiltcorrutinescicerone.util.extension.setOnDebouncedClickListener

class PhotoGallerySearchAdapter :
        ListAdapter<ItemPhoto, PhotoGallerySearchViewHolder>(PhotoGallerySearchDiffCallback()) {

    var onClickListener: ((ItemPhoto) -> Unit) = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGallerySearchViewHolder {
        val binding =
            ItemPhotoGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoGallerySearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoGallerySearchViewHolder, position: Int) {
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
