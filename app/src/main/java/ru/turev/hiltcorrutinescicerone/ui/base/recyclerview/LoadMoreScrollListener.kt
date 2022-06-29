package ru.turev.hiltcorrutinescicerone.ui.base.recyclerview

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.turev.hiltcorrutinescicerone.util.extension.castTo
import ru.turev.hiltcorrutinescicerone.util.extension.invokeIf

class LoadMoreScrollListener(
    private val loadMoreAction: () -> Unit
) : RecyclerView.OnScrollListener() {

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        loadMoreIfNeeded(
            recyclerView.adapter!!.itemCount,
            recyclerView
                .layoutManager
                .castTo<LinearLayoutManager>()
                ?.findLastVisibleItemPosition()
                ?: recyclerView
                    .layoutManager
                    .castTo<GridLayoutManager>()
                    ?.findLastVisibleItemPosition() ?: RecyclerView.NO_POSITION
        )
    }

    private fun loadMoreIfNeeded(itemsCount: Int, lastVisiblePosition: Int) {
        invokeIf(
            { lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition >= itemsCount - LOAD_MORE_THRESHOLD },
            loadMoreAction::invoke
        )
    }
}
