package com.example.library_with_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Item>>(generateItems())
    val items: LiveData<List<Item>> = _items

    private val _selectedItem = MutableLiveData<Item?>()
    val selectedItem: LiveData<Item?> = _selectedItem

    private val _scrollPosition = MutableLiveData<Int>()
    val scrollPosition: LiveData<Int> = _scrollPosition

    private val _scrollToLast = MutableLiveData(false)
    val scrollToLast: LiveData<Boolean> = _scrollToLast

    var currentlyEditingItem: Item? = null
    var isInEditMode: Boolean = false

    fun selectItem(item: Item) {
        _selectedItem.value = item
    }

    fun addItem(item: Item) {
        val currentItems = _items.value?.toMutableList() ?: mutableListOf()
        currentItems.add(item)
        _items.value = currentItems

        _scrollPosition.value = currentItems.size - 1
        _scrollToLast.value = true
    }

    fun setScrollPos(position: Int) {
        _scrollPosition.value = position
    }

    fun resetScrollFlag() {
        _scrollToLast.value = false
    }

    private fun generateItems(): List<Item> {
        return listOf(
            Book(1, "mybook1", "I", 20, true, R.drawable.book_image),
            Disk(2, "diisk1", "DVD", false, R.drawable.disk_image),
            Disk(22, "diisk2", "СD", true, R.drawable.disk_image),
            Disk(23, "diisk3", "DVD", true, R.drawable.disk_image),
            Newspaper(31, "news1", 412, "April", true, R.drawable.newspaper_image),
            Newspaper(32, "news2", 412, "March", true, R.drawable.newspaper_image),
            Newspaper(33, "news3", 412, "June", true, R.drawable.newspaper_image),
            Book(12, "mybook2", "I", 20, false, R.drawable.book_image),
            Book(123, "mybook3", "I", 20, true, R.drawable.book_image)
        )
    }
}
