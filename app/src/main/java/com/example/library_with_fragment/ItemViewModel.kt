package com.example.library_with_fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.math.log
import kotlin.random.Random
import kotlin.random.nextInt

class ItemViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    private val _selectedItem = MutableLiveData<Item?>()
    val selectedItem: LiveData<Item?> = _selectedItem

    private val _scrollPosition = MutableLiveData<Int>()
    val scrollPosition: LiveData<Int> = _scrollPosition

    private val _scrollToLast = MutableLiveData(false)
    val scrollToLast: LiveData<Boolean> = _scrollToLast

    var currentlyEditingItem: Item? = null
    var isInEditMode: Boolean = false
    private val _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading: MutableStateFlow<Boolean> = _isLoading

    private val _errorEvent = MutableLiveData<String?>()
    val errorEvent: LiveData<String?> = _errorEvent

    private var errorPeriod  = getRandom()
    private var operationCount: Int = 0

     fun selectItem(item: Item) {
        try {
            operationCount++
            Log.d("!!!", "Operation: $operationCount,  Period: ${errorPeriod}")
            if (operationCount >= errorPeriod) {
                Log.d("!!!", "FAKE EXEPTION")
                errorPeriod = getRandom()
                throw IllegalStateException("Random error")
            }
            _selectedItem.value = item
        }catch (e: Exception){
            operationCount=0
            _errorEvent.postValue("Failed to add item: ${e.message}")
        }
    }
    suspend fun addItem(item: Item) = withContext(Dispatchers.IO){
        try {
            operationCount++
            Log.d("!!!", "Operation: $operationCount,  Period: ${errorPeriod}")
            if (operationCount >= errorPeriod ) {
                Log.d("!!!", "FAKE EXEPTION")
                errorPeriod  = getRandom()
                throw IllegalStateException("Random error")
            }
            val currentItems = _items.value?.toMutableList() ?: mutableListOf()
            currentItems.add(item)
            _items.value = currentItems
            _scrollPosition.value = currentItems.size - 1
            _scrollToLast.value = true
        } catch (e: Exception) {
            operationCount=0
            _errorEvent.postValue("Failed to add item: ${e.message}")
        }
    }

    fun clearError() {
        _errorEvent.value = null
    }


    fun setScrollPos(position: Int) {
        _scrollPosition.value = position
    }

    fun resetScrollFlag() {
        _scrollToLast.value = false
    }

    suspend fun loadItems() = withContext(Dispatchers.IO){
        viewModelScope.launch {
            try {
                operationCount++
                Log.d("!!!", "Operation: $operationCount,  Period: ${errorPeriod}")
                if (operationCount >= errorPeriod) {
                    Log.d("!!!", "FAKE EXEPTION")
                    errorPeriod  = getRandom()
                    throw IllegalStateException("Random error")
                }
                _isLoading.value = true
                delay(Random.nextLong(from = 100, until = 2001))
                _items.value = generateItems()
                _isLoading.value = false
            }
            catch (e: Exception){
                operationCount=0
                _errorEvent.postValue("Failed to add item: ${e.message}")
            }
        }
    }
    private fun getRandom(): Int  =  Random.nextInt(2, 6)

    fun generateItems(): List<Item> {
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
