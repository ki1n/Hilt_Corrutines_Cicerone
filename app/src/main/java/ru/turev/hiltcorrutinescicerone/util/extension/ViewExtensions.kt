package ru.turev.hiltcorrutinescicerone.util.extension

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment

fun View.showKeyboard() {
    post {
        requestFocus()
        val inputMethodManager = context.getSystemService<InputMethodManager>()
        inputMethodManager?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun SwitchCompat.setChecked(flag: Boolean, listener: (Any, Boolean) -> Unit) {
    this.setOnCheckedChangeListener(null)
    this.isChecked = flag
    this.setOnCheckedChangeListener(listener)
}
