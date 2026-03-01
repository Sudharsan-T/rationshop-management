package com.example.meh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meh.data.RationViewModel
import com.example.meh.data.Sale
import com.example.meh.databinding.FragmentCartBinding
import java.text.SimpleDateFormat
import java.util.*

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CartAdapter(
            onQuantityChange = { cartItem, delta ->
                val result = viewModel.updateCartQuantity(cartItem.productId, delta)
                if (!result.first) {
                    Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                }
            },
            onRemoveClick = { cartItem ->
                viewModel.removeFromCart(cartItem.id)
            }
        )

        binding.rvCart.layoutManager = LinearLayoutManager(context)
        binding.rvCart.adapter = adapter

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            updateTotals(items)
            
            if (items.isEmpty()) {
                binding.tvEmptyCart.visibility = View.VISIBLE
                binding.rvCart.visibility = View.GONE
            } else {
                binding.tvEmptyCart.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
            }
        }

        binding.btnCheckout.setOnClickListener {
            val items = viewModel.cartItems.value
            val user = viewModel.currentUser.value
            if (!items.isNullOrEmpty() && user != null) {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                val invoiceId = "INV-" + System.currentTimeMillis().toString().takeLast(6)
                items.forEach { item ->
                    val sale = Sale(
                        productId = item.productId,
                        productName = item.productName,
                        userId = user.uid,
                        quantity = item.quantity,
                        totalPrice = item.quantity * item.unitPrice,
                        date = date,
                        unit = item.unit,
                        invoiceId = invoiceId
                    )
                    viewModel.recordSale(sale)
                }
                viewModel.clearCart()
                Toast.makeText(context, "Purchase successful! Invoice: $invoiceId", Toast.LENGTH_LONG).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun updateTotals(items: List<com.example.meh.data.CartItem>) {
        val total = items.sumOf { it.quantity * it.unitPrice }
        binding.tvGrandTotal.text = String.format(Locale.getDefault(), "₹ %.2f", total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
