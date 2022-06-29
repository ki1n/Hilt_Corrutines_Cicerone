package ru.turev.hiltcorrutinescicerone.ui.photoGallery.delegates

import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.turev.hiltcorrutinescicerone.databinding.ItemPhotoGalleryBinding
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto

fun photosAdapterDelegate(
    onItemClick: (ItemPhoto) -> Unit
) =
    adapterDelegateViewBinding<ItemPhoto, ItemPhoto, ItemPhotoGalleryBinding>(
        viewBinding = { layoutInflater, root ->
            ItemPhotoGalleryBinding.inflate(layoutInflater, root, false)
        }
    ) {
        itemView.setOnClickListener {
            onItemClick.invoke(item)
        }

        bind {
            with(binding) {
                Glide.with(imgItemPhoto.context)
                    .load(item.small)
                    .into(imgItemPhoto)

                imgInput.text = item.name
            }
        }
    }
