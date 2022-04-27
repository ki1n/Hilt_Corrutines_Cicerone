package ru.turev.hiltcorrutinescicerone.ui.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.databinding.FragmentPhotoGalleryBinding
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment

@AndroidEntryPoint
open class PhotoGalleryFragment : BaseFragment<FragmentPhotoGalleryBinding>() {

    protected open val viewModel: PhotoGalleryViewModel by viewModels()

    override fun getBinding(container: ViewGroup?) = FragmentPhotoGalleryBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
