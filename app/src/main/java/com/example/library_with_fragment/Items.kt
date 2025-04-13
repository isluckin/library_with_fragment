//package com.example.library_with_fragment
//
//import android.os.Parcelable
//import kotlinx.parcelize.Parcelize
//
//
//sealed class Item(
//    open val itemId: Int,
//    open val itemName: String,
//    open val isAvailable: Boolean,
//    open val imageRes: Int
//) : Parcelable
//
//@Parcelize
//data class Book(
//    override val itemId: Int,
//    override val itemName: String,
//    val bookAuthor: String,
//    val bookPages: Int,
//    override val isAvailable: Boolean,
//    override val imageRes: Int
//) : Item(itemId, itemName, isAvailable, imageRes) {
//    companion object {
//        fun createEmptyBook(): Book {
//            return Book(0, "", "", 0, true, R.drawable.book_image)
//        }
//
//
//    }
//
//}
//
//@Parcelize
//data class Newspaper(
//    override val itemId: Int,
//    override val itemName: String,
//    val newspaperNumber: Int,
//    val month: String,
//    override val isAvailable: Boolean,
//    override val imageRes: Int
//) : Item(itemId, itemName, isAvailable, imageRes) {
//    companion object {
//        fun createEmptyNewspaper(): Newspaper {
//            return Newspaper(0, "", 0, "", true, R.drawable.newspaper_image)
//        }
//    }
//}
//
//@Parcelize
//data class Disk(
//    override val itemId: Int,
//    override val itemName: String,
//    val diskType: String,
//    override val isAvailable: Boolean,
//    override val imageRes: Int
//) : Item(itemId, itemName, isAvailable, imageRes) {
//    companion object {
//        fun createEmptyDisk(): Disk {
//            return Disk(0, "", "", true, R.drawable.disk_image)
//        }
//    }
//}