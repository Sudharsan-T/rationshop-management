package com.example.meh

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meh.data.CartItem
import com.example.meh.databinding.ItemCartBinding

class CartAdapter(
    private val onQuantityChange: (CartItem, Double) -> Unit,
    private val onRemoveClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.tvCartName.text = item.productName
            binding.tvCartPrice.text = "₹ ${item.quantity * item.unitPrice}"
            binding.tvCartQty.text = item.quantity.toString()
            
            binding.btnCartPlus.setOnClickListener { onQuantityChange(item, 1.0) }
            binding.btnCartMinus.setOnClickListener { onQuantityChange(item, -1.0) }
            binding.btnRemove.setOnClickListener { onRemoveClick(item) }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem == newItem
    }
}
