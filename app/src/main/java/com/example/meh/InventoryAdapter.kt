package com.example.meh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meh.data.Product
import com.example.meh.databinding.ItemInventoryBinding

/**
 * Adapter for managing inventory in the Admin/Shopkeeper panel.
 * Displays products and provides controls based on user role.
 */
class InventoryAdapter(
    private val userRole: String,
    private val onAddStock: (Product, Int) -> Unit,
    private val onEditProduct: (Product) -> Unit,
    private val onDeleteProduct: (Product) -> Unit
) : ListAdapter<Product, InventoryAdapter.InventoryViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InventoryViewHolder(private val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(product: Product) {
            binding.tvInvName.text = product.name
            binding.tvInvStock.text = "Current Stock: ${product.stock} ${product.unit}"
            
            // Shopkeeper and Admin can both add stock
            binding.btnAddStock.setOnClickListener {
                val amountStr = binding.etAddAmount.text.toString()
                if (amountStr.isNotEmpty()) {
                    onAddStock(product, amountStr.toInt())
                    binding.etAddAmount.text?.clear()
                }
            }

            // Admin only features: Edit and Delete
            if (userRole == "ADMIN") {
                binding.llAdminActions.visibility = View.VISIBLE
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
                
                binding.btnEdit.setOnClickListener { onEditProduct(product) }
                binding.btnDelete.setOnClickListener { onDeleteProduct(product) }
            } else {
                binding.llAdminActions.visibility = View.GONE
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}
