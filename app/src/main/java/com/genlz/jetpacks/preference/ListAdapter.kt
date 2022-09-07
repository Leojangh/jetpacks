package com.genlz.jetpacks.preference

abstract class ListAdapter<T, R>(
    private var models: List<T>,
) {

    private val itemHolder: List<R> = mutableListOf()

    open fun getItemType(position: Int) = 0

    abstract fun onCreateItem(parent: Listable, itemType: Int): R

    abstract fun onBindItem(item: R, model: T)

    open fun getItemCount() = models.size

    fun submitList(data: List<T>) {
        models = data
    }

    fun getItem(position: Int) = models[position]
}