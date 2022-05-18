package ru.turev.hiltcorrutinescicerone.ui.base

import android.view.animation.Animation
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.util.extension.hideKeyboard

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        view?.translationZ = if (enter && (nextAnim == R.anim.slide_in_right || nextAnim == R.anim.fade_in)) 1f else 0f
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    protected fun <T> LiveData<T>.observe(observer: Observer<T>) {
        return observe(viewLifecycleOwner, observer)
    }
}
