package ru.turev.hiltcorrutinescicerone.extension

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

val writeGalleryPermission: String
    get() = Manifest.permission.WRITE_EXTERNAL_STORAGE
val readGalleryPermission: String
    get() = Manifest.permission.READ_EXTERNAL_STORAGE

private fun Fragment.isPermissionGranted(permission: String) =
    ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

fun Fragment.isGalleryPermissionGranted() =
    isPermissionGranted(writeGalleryPermission) && isPermissionGranted(readGalleryPermission)

fun Fragment.requestGalleryPermission(): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), writeGalleryPermission)
}

fun Fragment.showToastShort(@StringRes messageResId: Int) =
    Toast.makeText(context, getString(messageResId), Toast.LENGTH_SHORT)
        .show()
