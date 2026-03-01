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
import com.example.meh.databinding.FragmentReportBinding
import java.util.Locale

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SalesAdapter { sale ->
            val bundle = Bundle().apply {
                putSerializable("sale", sale)
            }
            findNavController().navigate(R.id.action_SalesFragment_to_InvoiceFragment, bundle)
        }
        
        binding.rvReportList.layoutManager = LinearLayoutManager(context)
        binding.rvReportList.adapter = adapter

        viewModel.totalRevenue.observe(viewLifecycleOwner) { revenue ->
            binding.tvTotalRevenue.text = String.format(Locale.getDefault(), "₹ %.2f", revenue ?: 0.0)
        }

        viewModel.allSales.observe(viewLifecycleOwner) { sales ->
            adapter.submitList(sales)
            if (sales.isEmpty()) {
                binding.tvNoReportData.visibility = View.VISIBLE
                binding.rvReportList.visibility = View.GONE
            } else {
                binding.tvNoReportData.visibility = View.GONE
                binding.rvReportList.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
