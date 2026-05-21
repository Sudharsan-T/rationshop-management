package com.example.meh

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meh.data.Product
import com.example.meh.data.RationViewModel
import com.example.meh.databinding.FragmentPurchaseBinding

class PurchaseFragment : Fragment() {

    private var _binding: FragmentPurchaseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()
    private var allProductsList = listOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvProducts.layoutManager = LinearLayoutManager(context)

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            val isStaff = user?.role == "ADMIN" || user?.role == "SHOPKEEPER"
            binding.fabGoToCart.visibility = if (isStaff) View.GONE else View.VISIBLE

            val adapter = ProductAdapter(isStaff) { product, delta ->
                val result = viewModel.updateCartQuantity(product.id, delta)
                if (!result.first) {
                    Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                }
            }
            binding.rvProducts.adapter = adapter
            adapter.setCartItems(viewModel.cartItems.value ?: emptyList())
            updateUI(allProductsList)
        }

        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            allProductsList = products
            updateUI(products)
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            (binding.rvProducts.adapter as? ProductAdapter)?.setCartItems(items)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = allProductsList.filter { 
                    it.name.lowercase().contains(query) 
                }
                updateUI(filteredList)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fabGoToCart.setOnClickListener {
            findNavController().navigate(R.id.action_PurchaseFragment_to_CartFragment)
        }
    }

    private fun updateUI(list: List<Product>) {
        val adapter = binding.rvProducts.adapter as? ProductAdapter
        adapter?.submitList(list)
        if (list.isEmpty()) {
            binding.tvNoProducts.visibility = View.VISIBLE
            binding.rvProducts.visibility = View.GONE
        } else {
            binding.tvNoProducts.visibility = View.GONE
            binding.rvProducts.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
