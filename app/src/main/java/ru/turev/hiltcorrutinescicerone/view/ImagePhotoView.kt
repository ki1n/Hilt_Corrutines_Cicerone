package ru.turev.hiltcorrutinescicerone.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import ru.turev.hiltcorrutinescicerone.domain.entity.ItemPhoto

class ImagePhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    fun setData(itemPhoto: ItemPhoto) {
        Glide.with(this.context)
            .load(itemPhoto.full)
            .into(this)
    }

}
