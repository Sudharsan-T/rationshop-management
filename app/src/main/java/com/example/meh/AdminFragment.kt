package com.example.meh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meh.data.Product
import com.example.meh.data.RationViewModel
import com.example.meh.databinding.FragmentAdminBinding

/**
 * Fragment for the Shopkeeper/Admin panel.
 * Allows managing inventory based on user permissions.
 */
class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = viewModel.currentUser.value
        val userRole = user?.role ?: "SHOPKEEPER"

        // Initialize adapter with callbacks for Add, Edit, and Delete
        val adapter = InventoryAdapter(
            userRole = userRole,
            onAddStock = { product, addedAmount ->
                viewModel.updateStock(product.id, addedAmount)
                Toast.makeText(context, "Added $addedAmount to ${product.name}", Toast.LENGTH_SHORT).show()
            },
            onEditProduct = { product ->
                showEditProductDialog(product)
            },
            onDeleteProduct = { product ->
                showDeleteConfirmation(product)
            }
        )

        binding.rvInventory.layoutManager = LinearLayoutManager(context)
        binding.rvInventory.adapter = adapter

        // Observe product list from ViewModel
        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
    }

    /**
     * Shows a dialog to edit product details (Admin only).
     */
    private fun showEditProductDialog(product: Product) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_admin, null)
        // Note: For a real app, you'd create a specific layout for this dialog.
        // For the demo, we can use a quick custom layout or just logic.
        
        val nameInput = EditText(context).apply { setText(product.name); hint = "Name" }
        val priceInput = EditText(context).apply { setText(product.price.toString()); hint = "Price" }
        val imageInput = EditText(context).apply { setText(product.imageUrl); hint = "Image URL" }
        
        val layout = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(nameInput)
            addView(priceInput)
            addView(imageInput)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Product")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val updatedProduct = product.copy(
                    name = nameInput.text.toString(),
                    price = priceInput.text.toString().toDoubleOrNull() ?: product.price,
                    imageUrl = imageInput.text.toString()
                )
                viewModel.updateProductDetails(updatedProduct)
                Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Confirms deletion of a product (Admin only).
     */
    private fun showDeleteConfirmation(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteProduct(product.id)
                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
