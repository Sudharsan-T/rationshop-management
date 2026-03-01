package com.example.meh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meh.data.RationViewModel
import com.example.meh.databinding.FragmentStockUpdatesBinding

class StockUpdatesFragment : Fragment() {

    private var _binding: FragmentStockUpdatesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockUpdatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = StockUpdateAdapter()
        binding.rvStockUpdates.layoutManager = LinearLayoutManager(context)
        binding.rvStockUpdates.adapter = adapter

        viewModel.stockUpdates.observe(viewLifecycleOwner) { updates ->
            adapter.submitList(updates)
            if (updates.isEmpty()) {
                binding.tvNoUpdates.visibility = View.VISIBLE
                binding.rvStockUpdates.visibility = View.GONE
            } else {
                binding.tvNoUpdates.visibility = View.GONE
                binding.rvStockUpdates.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
