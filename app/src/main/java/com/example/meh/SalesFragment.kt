package com.example.meh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meh.data.RationViewModel
import com.example.meh.databinding.FragmentSalesBinding

/**
 * Fragment for viewing Sales or Purchase History.
 * Shopkeepers and Admins see all transactions, while Customers see only their own.
 */
class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup adapter with click listener to view detailed invoice
        val adapter = SalesAdapter { sale ->
            val bundle = Bundle().apply {
                putSerializable("sale", sale)
            }
            findNavController().navigate(R.id.action_SalesFragment_to_InvoiceFragment, bundle)
        }

        binding.rvSales.layoutManager = LinearLayoutManager(context)
        binding.rvSales.adapter = adapter

        // Filter and display history based on user role
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Staff members (ADMIN and SHOPKEEPER) see "Sales History"
                val isStaff = user.role == "ADMIN" || user.role == "SHOPKEEPER"
                binding.tvSalesTitle.text = if (isStaff) "Sales History" else "Purchase History"
                
                viewModel.allSales.observe(viewLifecycleOwner) { sales ->
                    val filteredSales = if (isStaff) {
                        // Staff sees everything
                        sales
                    } else {
                        // Customers see only their records
                        sales.filter { it.userId == user.uid }
                    }
                    
                    adapter.submitList(filteredSales)
                    
                    // Handle empty state UI
                    if (filteredSales.isEmpty()) {
                        binding.tvNoSales.visibility = View.VISIBLE
                        binding.rvSales.visibility = View.GONE
                    } else {
                        binding.tvNoSales.visibility = View.GONE
                        binding.rvSales.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
