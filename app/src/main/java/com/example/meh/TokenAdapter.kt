package com.example.meh

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meh.data.Token
import com.example.meh.databinding.ItemTokenBinding

class TokenAdapter : ListAdapter<Token, TokenAdapter.TokenViewHolder>(TokenDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokenViewHolder {
        val binding = ItemTokenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TokenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TokenViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TokenViewHolder(private val binding: ItemTokenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(token: Token) {
            binding.tvTokenDate.text = "Date: ${token.date}"
            binding.tvTokenSlot.text = "Slot: ${token.slot}"
            binding.tvTokenStatus.text = "Status: ${token.status}"
        }
    }

    class TokenDiffCallback : DiffUtil.ItemCallback<Token>() {
        override fun areItemsTheSame(oldItem: Token, newItem: Token) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Token, newItem: Token) = oldItem == newItem
    }
}
