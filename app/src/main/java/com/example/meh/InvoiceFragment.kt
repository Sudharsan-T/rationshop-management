package com.example.meh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.meh.data.RationViewModel
import com.example.meh.data.Sale
import com.example.meh.databinding.FragmentInvoiceBinding
import java.util.Locale

class InvoiceFragment : Fragment() {

    private var _binding: FragmentInvoiceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sale = arguments?.getSerializable("sale") as? Sale ?: return

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvCustomerName.text = user.name
                binding.tvRationCard.text = user.rationCardNumber
            }
        }

        binding.tvInvoiceId.text = "#${sale.invoiceId}"
        binding.tvInvoiceDate.text = sale.date
        binding.tvItemName.text = sale.productName
        binding.tvItemQty.text = "${sale.quantity} ${sale.unit}"
        binding.tvItemTotal.text = String.format(Locale.getDefault(), "₹ %.2f", sale.totalPrice)
        binding.tvGrandTotal.text = String.format(Locale.getDefault(), "₹ %.2f", sale.totalPrice)

        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
