package com.example.library_with_fragment

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Item(
    open val itemId: Int,
    open val itemName: String,
    open val isAvailable: Boolean,
    open val imageRes: Int,
    open val createdAt: Long = System.currentTimeMillis()
) : Parcelable


@Parcelize
data class Book(
    override val itemId: Int = 0,
    override val itemName: String,
    val bookAuthor: String,
    val bookPages: Int,
    override val isAvailable: Boolean,
    override val imageRes: Int = R.drawable.book_image,
    override val createdAt: Long = System.currentTimeMillis()
) : Item(itemId, itemName, isAvailable, imageRes, createdAt) {


    companion object {
        fun createEmptyBook() = Book(
            itemName = "", bookAuthor = "", bookPages = 0, isAvailable = true
        )
    }
}

@Parcelize
data class Newspaper(
    override val itemId: Int = 0,
    override val itemName: String,
    val newspaperNumber: Int,
    val month: String,
    override val isAvailable: Boolean,
    override val imageRes: Int = R.drawable.newspaper_image,
    override val createdAt: Long = System.currentTimeMillis()
) : Item(itemId, itemName, isAvailable, imageRes, createdAt) {


    companion object {

        fun createEmptyNewspaper() = Newspaper(
            itemName = "", newspaperNumber = 0, month = "", isAvailable = true
        )
    }
}

@Parcelize
data class Disk(
    override val itemId: Int = 0,
    override val itemName: String,
    val diskType: String,
    override val isAvailable: Boolean,
    override val imageRes: Int = R.drawable.disk_image,
    override val createdAt: Long = System.currentTimeMillis()
) : Item(itemId, itemName, isAvailable, imageRes, createdAt) {


    companion object {
        fun createEmptyDisk() = Disk(
            itemName = "", diskType = "", isAvailable = true
        )
    }
}

@Entity(tableName = "base_items")
data class BaseItemEntity(
    @PrimaryKey val id: Int,
    val type: String,
    val name: String,
    val isAvailable: Boolean,
    val imageRes: Int,
    val createdAt: Long
)


@Entity(tableName = "book_details", primaryKeys = ["itemId"])
data class BookDetailsEntity(
    val itemId: Int, val author: String, val pageCount: Int
)


@Entity(tableName = "newspaper_details", primaryKeys = ["itemId"])
data class NewspaperDetailsEntity(
    val itemId: Int, val issueDate: String, val number: Int
)


@Entity(tableName = "disk_details", primaryKeys = ["itemId"])
data class DiskDetailsEntity(
    val itemId: Int, val diskType: String
)