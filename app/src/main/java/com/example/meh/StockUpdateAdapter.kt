package com.example.meh

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meh.data.StockUpdate
import com.example.meh.databinding.ItemStockUpdateBinding

class StockUpdateAdapter : ListAdapter<StockUpdate, StockUpdateAdapter.StockUpdateViewHolder>(StockUpdateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockUpdateViewHolder {
        val binding = ItemStockUpdateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockUpdateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockUpdateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StockUpdateViewHolder(private val binding: ItemStockUpdateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(update: StockUpdate) {
            binding.tvUpdateDate.text = update.date
            binding.tvUpdateProductName.text = update.productName
            binding.tvUpdateDetails.text = "Added: +${update.addedAmount} ${update.unit}"
            binding.tvTotalStock.text = "New Total: ${update.totalStock} ${update.unit}"
        }
    }

    class StockUpdateDiffCallback : DiffUtil.ItemCallback<StockUpdate>() {
        override fun areItemsTheSame(oldItem: StockUpdate, newItem: StockUpdate) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: StockUpdate, newItem: StockUpdate) = oldItem == newItem
    }
}
