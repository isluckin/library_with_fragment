package com.example.library_with_fragment

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ItemViewModel(context: Context) : ViewModel() {
    private val repository: ItemRepository

    enum class SortType { BY_NAME, BY_DATE }


    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    private val _selectedItem = MutableLiveData<Item?>()
    val selectedItem: LiveData<Item?> = _selectedItem

    private val _scrollPosition = MutableLiveData<Int>()
    val scrollPosition: LiveData<Int> = _scrollPosition

    private val _scrollToLast = MutableLiveData(false)
    val scrollToLast: LiveData<Boolean> = _scrollToLast

    private val _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading: MutableStateFlow<Boolean> = _isLoading

    private val _errorEvent = MutableLiveData<String?>()
    val errorEvent: LiveData<String?> = _errorEvent
    private val _sortType = MutableStateFlow<SortType>(SortType.BY_NAME)
    private var initialLoadCount = 30
    private var pageSize = initialLoadCount / 2
    private var threshold = 10
    private var currentOffset = 0
    private var isForwardPagination = true
    private var totalItemsInDb = 0

    private var errorPeriod = getRandom()
    private var operationCount: Int = 0

    var currentlyEditingItem: Item? = null
    var isInEditMode: Boolean = false

    init {
        val db = AppDatabase.getDatabase(context)

        repository = ItemRepository(db.itemDao(), db.sortPreferenceDao())

        viewModelScope.launch {
            val savedSortType = repository.getSortPreference()
            _sortType.value = savedSortType
            loadInitialItems()
        }
    }

    private suspend fun loadInitialItems() = withContext(Dispatchers.IO) {

        _isLoading.value = true

        val items = when (_sortType.value) {

            SortType.BY_NAME -> repository.getItemsSortedByName(0, initialLoadCount)
            else -> repository.getItemsSortedByDate(0, initialLoadCount)
        }
        _items.postValue(items)
        currentOffset = items.size
        _isLoading.value = false
    }

    fun setSortType(type: SortType) {
        viewModelScope.launch {
            repository.saveSortPreference(type)
            _sortType.value = type

            refreshData()
        }
    }

    fun getSortType(): SortType {
        return _sortType.value
    }

    private fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            currentOffset = 0
            val sortType = _sortType.value
            val items = when (sortType) {
                SortType.BY_NAME -> repository.getItemsSortedByName(0, initialLoadCount)
                else -> repository.getItemsSortedByDate(0, initialLoadCount)
            }
            _items.postValue(items)
            currentOffset = items.size
            _isLoading.value = false
        }
    }

    suspend fun loadItems() = withContext(Dispatchers.IO) {
        viewModelScope.launch {
            try {
                operationCount++
                Log.d("!!!", "Operation: $operationCount, Period: ${errorPeriod}")
                if (operationCount >= errorPeriod) {
                    Log.d("!!!", "FAKE EXCEPTION")
                    errorPeriod = getRandom()
                    throw IllegalStateException("Random error")
                }

                _isLoading.value = true
                delay(Random.nextLong(from = 100, until = 2001))


                totalItemsInDb = repository.getAllItemsCount()
                val loadedItems = repository.getItemsWithLimit(0, initialLoadCount)

                _items.value = loadedItems
                currentOffset = loadedItems.size
                _isLoading.value = false

            } catch (e: Exception) {
                operationCount = 0
                _errorEvent.postValue("Failed to load items: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadMoreItemsForward() = withContext(Dispatchers.IO) {
        if (_isLoading.value) return@withContext

        try {
            _isLoading.value = true
            isForwardPagination = true

            val newItems = repository.getItemsWithLimit(currentOffset, pageSize)
            if (newItems.isEmpty()) {
                _isLoading.value = false
                return@withContext
            }

            val currentItems = _items.value?.toMutableList() ?: mutableListOf()
            currentItems.addAll(newItems)
            val itemsToRemove = minOf(pageSize, currentItems.size - newItems.size)
            if (itemsToRemove > 0) {
                currentItems.subList(0, itemsToRemove).clear()
            }

            _items.postValue(currentItems)
            currentOffset += newItems.size

            _isLoading.value = false
        } catch (e: Exception) {
            _errorEvent.postValue("Failed to load more items: ${e.message}")
            _isLoading.value = false
        }
    }

    private suspend fun loadMoreItemsBackward() = withContext(Dispatchers.IO) {
        if (_isLoading.value) return@withContext

        try {
            _isLoading.value = true
            isForwardPagination = false
            val newOffset = maxOf(0, currentOffset - initialLoadCount - pageSize)
            val loadSize = minOf(pageSize, currentOffset - newOffset)

            if (loadSize <= 0) {
                _isLoading.value = false
                return@withContext
            }
            val newItems = repository.getItemsWithLimit(newOffset, loadSize)
            if (newItems.isEmpty()) {
                _isLoading.value = false
                return@withContext
            }
            val currentItems = _items.value?.toMutableList() ?: mutableListOf()
            currentItems.addAll(0, newItems)
            val itemsToRemove = minOf(pageSize, currentItems.size - newItems.size)
            if (itemsToRemove > 0) {
                currentItems.subList(currentItems.size - itemsToRemove, currentItems.size).clear()
            }

            _items.postValue(currentItems)
            currentOffset = newOffset

            _isLoading.value = false
        } catch (e: Exception) {
            _errorEvent.postValue("Failed to load previous items: ${e.message}")
            _isLoading.value = false
        }
    }

    fun checkPagination(currentIndex: Int) {
        val currentItems = _items.value ?: return

        if (isForwardPagination && currentIndex >= currentItems.size - threshold) {
            viewModelScope.launch { loadMoreItemsForward() }
        } else if (!isForwardPagination && currentIndex <= threshold) {
            viewModelScope.launch { loadMoreItemsBackward() }
        }
    }

    fun selectItem(item: Item) {
        try {
            operationCount++
            Log.d("!!!", "Operation: $operationCount, Period: ${errorPeriod}")
            if (operationCount >= errorPeriod) {
                Log.d("!!!", "FAKE EXCEPTION")
                errorPeriod = getRandom()
                throw IllegalStateException("Random error")
            }
            _selectedItem.value = item
        } catch (e: Exception) {
            operationCount = 0
            _errorEvent.postValue("Failed to select item: ${e.message}")
        }
    }

    suspend fun addItem(item: Item) = withContext(Dispatchers.IO) {
        try {
            operationCount++
            Log.d("!!!", "Operation: $operationCount, Period: ${errorPeriod}")
            if (operationCount >= errorPeriod) {
                Log.d("!!!", "FAKE EXCEPTION")
                errorPeriod = getRandom()
                throw IllegalStateException("Random error")
            }
            when (item) {
                is Book -> repository.saveBook(item)
                is Newspaper -> repository.saveNewspaper(item)
                is Disk -> repository.saveDisk(item)
            }

            totalItemsInDb = repository.getAllItemsCount()
            val currentItems = _items.value?.toMutableList() ?: mutableListOf()
            currentItems.add(item)
            _items.postValue(currentItems)
            _scrollPosition.postValue(currentItems.size - 1)
            _scrollToLast.postValue(true)
        } catch (e: Exception) {
            operationCount = 0
            _errorEvent.postValue("Failed to add item: ${e.message}")
        }
    }

    fun clearError() {
        _errorEvent.value = null
    }

    fun setScrollPos(position: Int) {
        _scrollPosition.value = position
        checkPagination(position)
    }

    fun resetScrollFlag() {
        _scrollToLast.value = false
    }

    private fun getRandom(): Int = Random.nextInt(2, 6)
}