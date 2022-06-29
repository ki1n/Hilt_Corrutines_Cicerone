package ru.turev.hiltcorrutinescicerone.ui.base.recyclerview

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ru.turev.hiltcorrutinescicerone.ui.base.recyclerview.adapter.IItemsHolder

open class BaseAdapter<Item : Any>(vararg delegates: AdapterDelegate<List<Item>>) : ListDelegationAdapter<List<Item>>(),
                                                                                    IItemsHolder<Item> {
    init {
        items = mutableListOf()
        delegates.forEach { delegatesManager.addDelegate(it) }
    }

    override fun setItems(items: List<Item>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }

    fun setItems(items: List<Item>?, notifyAdapter: Boolean) {
        super.setItems(items)
        if (notifyAdapter) {
            notifyDataSetChanged()
        }
    }
}
