package ru.turev.hiltcorrutinescicerone.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import ru.turev.hiltcorrutinescicerone.R

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    protected open var binding: T? = null

    protected abstract fun getBinding(container: ViewGroup?): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getBinding(container)

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        view?.translationZ = if (enter && (nextAnim == R.anim.slide_in_right || nextAnim == R.anim.fade_in)) 1f else 0f
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    protected fun <T> LiveData<T>.observe(observer: Observer<T>) {
        return observe(viewLifecycleOwner, observer)
    }
}
