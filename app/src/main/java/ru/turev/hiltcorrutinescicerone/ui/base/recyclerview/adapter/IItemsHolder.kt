package ru.turev.hiltcorrutinescicerone.ui.base.recyclerview.adapter

interface IItemsHolder<T : Any> {
    fun getItems(): List<T>
}
