package ru.turev.hiltcorrutinescicerone.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.ui.photo.PhotoGalleryFragment

class Navigator(activity: FragmentActivity, containerId: Int) : AppNavigator(activity, containerId) {

    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
        val enter = when {
            currentFragment == null && nextFragment is PhotoGalleryFragment -> 0
//            nextFragment is ProcessingFragment -> R.anim.fade_in
//            nextFragment is EditorFragment -> R.anim.idle
            else -> R.anim.slide_in_right
        }
        val exit = when (nextFragment) {
//            is FaceSelectionFragment -> R.anim.slide_out_right
//            is ProcessingFragment -> R.anim.fade_out
            else -> R.anim.slide_out_left
        }
        val popEnter = when (nextFragment) {
//            is ProcessingFragment -> R.anim.idle
            else -> R.anim.slide_in_left
        }
        val popExit = when (nextFragment) {
//            is ProcessingFragment -> R.anim.fade_out
            else -> R.anim.slide_out_right
        }

        fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit)
    }
}
