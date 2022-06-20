package ru.turev.hiltcorrutinescicerone.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.turev.hiltcorrutinescicerone.R
import ru.turev.hiltcorrutinescicerone.databinding.FragmentSearchBinding
import ru.turev.hiltcorrutinescicerone.ui.base.BaseFragment
import ru.turev.hiltcorrutinescicerone.ui.base.binding.viewBinding

@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.fragment_search) {

    companion object {
        fun getInstance() = SearchFragment()
    }

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            etSearch.doAfterTextChanged { searchInput ->
                viewModel.onSearchInputUpdate(searchInput.toString())
            }
        }
    }
}
