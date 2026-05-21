package com.example.meh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meh.data.RationViewModel
import com.example.meh.data.Token
import com.example.meh.databinding.FragmentTokenBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for managing slot tokens.
 * Customers can book slots, while Shopkeepers and Admins can view all scheduled tokens.
 */
class TokenFragment : Fragment() {

    private var _binding: FragmentTokenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTokenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the dropdown for time slots
        val slots = arrayOf("10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM", "02:00 PM - 03:00 PM", "03:00 PM - 04:00 PM")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, slots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSlots.adapter = adapter

        // Update selected date when user clicks on the calendar
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        }

        // Setup the list to display tokens
        var tokenAdapter = TokenAdapter()
        binding.rvTokens.layoutManager = LinearLayoutManager(context)
        binding.rvTokens.adapter = tokenAdapter

        // Control UI visibility and data filtering based on user role
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val isStaff = user.role == "ADMIN" || user.role == "SHOPKEEPER"

                if (isStaff) {
                    tokenAdapter = TokenAdapter()
                    binding.rvTokens.adapter = tokenAdapter
                    binding.tvTokenTitle.text = "Bookings"
                    binding.tvSelectSlotLabel.visibility = View.GONE
                    binding.btnBookToken.visibility = View.GONE
                    binding.spinnerSlots.visibility = View.GONE
                    binding.calendarView.visibility = View.GONE
                    binding.tvListHeader.text = "All Bookings:"
                } else {
                    tokenAdapter = TokenAdapter(onCancel = { token ->
                        viewModel.cancelToken(token.id)
                    })
                    binding.rvTokens.adapter = tokenAdapter
                    binding.tvTokenTitle.text = "Book Your Token"
                    binding.tvSelectSlotLabel.visibility = View.VISIBLE
                    binding.btnBookToken.visibility = View.VISIBLE
                    binding.spinnerSlots.visibility = View.VISIBLE
                    binding.calendarView.visibility = View.VISIBLE
                    binding.tvListHeader.text = "Your Bookings:"
                }
            }
        }

        // Single tokens observer — filters based on current user to avoid stacking observers
        viewModel.allTokens.observe(viewLifecycleOwner) { tokens ->
            val user = viewModel.currentUser.value ?: return@observe
            val isStaff = user.role == "ADMIN" || user.role == "SHOPKEEPER"
            tokenAdapter.submitList(if (isStaff) tokens else tokens.filter { it.userId == user.uid })
        }

        // Handle booking action
        binding.btnBookToken.setOnClickListener {
            val user = viewModel.currentUser.value
            if (user != null) {
                val token = Token(
                    userId = user.uid,
                    date = selectedDate,
                    slot = binding.spinnerSlots.selectedItem.toString(),
                    status = "BOOKED"
                )
                viewModel.bookToken(token)
                Toast.makeText(context, "Token booked for $selectedDate", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
