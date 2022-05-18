package ru.turev.hiltcorrutinescicerone.util.extension

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

fun Context.getCompatColor(@ColorRes res: Int): Int = ContextCompat.getColor(this, res)

fun Context.getCompatDrawable(@DrawableRes res: Int): Drawable? =
    AppCompatResources.getDrawable(this, res)
