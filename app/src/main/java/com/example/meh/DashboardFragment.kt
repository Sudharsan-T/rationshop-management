package com.example.meh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.meh.data.RationViewModel
import com.example.meh.databinding.FragmentDashboardBinding

/**
 * Dashboard screen that acts as the main hub for the user.
 * Displays different options based on the user's role (ADMIN, SHOPKEEPER, or CUSTOMER).
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the user's profile to dynamically update the UI
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvWelcome.text = getString(R.string.welcome_user, user.name)
                binding.tvRole.text = user.role
                
                // Show/Hide buttons based on Role
                when (user.role) {
                    "ADMIN", "SHOPKEEPER" -> {
                        // Both Admin and Shopkeeper can manage products and see history/logs
                        binding.btnAdminPanel.visibility = View.VISIBLE
                        binding.btnReport.visibility = View.VISIBLE
                        binding.btnStockUpdates.visibility = View.VISIBLE
                        
                        binding.btnAdminPanel.text = "Manage Products"
                        binding.btnSales.text = "Sales History"
                        binding.btnTokenBooking.text = "Scheduled Tokens"
                        binding.fabCart.visibility = View.GONE
                    }
                    else -> {
                        // Customer view
                        binding.btnAdminPanel.visibility = View.GONE
                        binding.btnReport.visibility = View.GONE
                        binding.btnStockUpdates.visibility = View.GONE
                        
                        binding.btnSales.text = "Purchase History"
                        binding.btnTokenBooking.text = "Token Booking"
                        binding.fabCart.visibility = View.VISIBLE
                    }
                }
            } else {
                // If user is null, redirect to Login screen
                if (findNavController().currentDestination?.id == R.id.DashboardFragment) {
                    findNavController().navigate(R.id.action_DashboardFragment_to_LoginFragment)
                }
            }
        }

        // Set up click listeners for all dashboard buttons
        binding.btnPurchase.setOnClickListener {
             findNavController().navigate(R.id.action_DashboardFragment_to_PurchaseFragment)
        }

        binding.btnTokenBooking.setOnClickListener {
             findNavController().navigate(R.id.action_DashboardFragment_to_TokenFragment)
        }

        binding.btnSales.setOnClickListener {
             findNavController().navigate(R.id.action_DashboardFragment_to_SalesFragment)
        }

        binding.btnReport.setOnClickListener {
             findNavController().navigate(R.id.action_DashboardFragment_to_ReportFragment)
        }

        binding.btnAdminPanel.setOnClickListener {
             findNavController().navigate(R.id.action_DashboardFragment_to_AdminFragment)
        }
        
        binding.btnStockUpdates.setOnClickListener {
            findNavController().navigate(R.id.action_DashboardFragment_to_StockUpdatesFragment)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.fabCart.setOnClickListener {
            findNavController().navigate(R.id.action_DashboardFragment_to_CartFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
