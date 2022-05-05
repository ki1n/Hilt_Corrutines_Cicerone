package ru.turev.hiltcorrutinescicerone.util.extension

import android.content.pm.PackageManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

@ColorInt
fun Fragment.getColor(@ColorRes id: Int): Int = ContextCompat.getColor(requireContext(), id)

fun Fragment.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

fun Fragment.showSnackbar(@StringRes resId: Int, duration: Int = Snackbar.LENGTH_LONG) {
    showSnackbar(getString(resId), duration)
}

fun Fragment.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    view?.run { Snackbar.make(this, message, duration).show() }
}
