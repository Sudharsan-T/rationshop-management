package com.example.meh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.meh.data.CartItem
import com.example.meh.data.Product
import com.example.meh.databinding.ItemProductBinding

class ProductAdapter(
    private val isAdmin: Boolean,
    private val onQuantityChange: (Product, Double) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private var cartItems: List<CartItem> = emptyList()

    fun setCartItems(items: List<CartItem>) {
        cartItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvItemName.text = product.name
            binding.tvItemPrice.text = "₹ ${product.price} / ${product.unit}"
            binding.tvItemStock.text = "Available: ${product.stock} ${product.unit}"
            binding.ivProduct.load(product.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
            
            if (isAdmin) {
                binding.btnAddToCart.visibility = View.GONE
                binding.llQtyControl.visibility = View.GONE
            } else {
                val cartItem = cartItems.find { it.productId == product.id }
                if (cartItem != null) {
                    binding.btnAddToCart.visibility = View.GONE
                    binding.llQtyControl.visibility = View.VISIBLE
                    binding.tvQty.text = cartItem.quantity.toString()
                } else {
                    binding.btnAddToCart.visibility = View.VISIBLE
                    binding.llQtyControl.visibility = View.GONE
                }

                binding.btnAddToCart.setOnClickListener { onQuantityChange(product, 1.0) }
                binding.btnPlus.setOnClickListener { onQuantityChange(product, 1.0) }
                binding.btnMinus.setOnClickListener { onQuantityChange(product, -1.0) }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}
