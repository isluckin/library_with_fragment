package com.example.library_with_fragment

import androidx.recyclerview.widget.RecyclerView
import com.example.library_with_fragment.databinding.LibraryItemBinding


class ItemViewHolder(
    private val binding: LibraryItemBinding, private val onClick: (Item) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Item) {
        with(binding) {
            itemName.text = item.itemName
            itemId.text = item.itemId.toString()
            itemImage.setImageResource(item.imageRes)
            root.setOnClickListener { onClick(item) }
        }

    }
}
