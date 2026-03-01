package com.example.meh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.meh.data.RationViewModel
import com.example.meh.data.User
import com.example.meh.databinding.FragmentRegisterBinding

/**
 * Fragment for user registration.
 * Allows users to create an account with personal details and a specific role.
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle dynamic hint change based on role selection
        binding.rgRole.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCustomer -> {
                    binding.etRationCard.hint = "Ration Card Number"
                }
                R.id.rbShopkeeper, R.id.rbAdmin -> {
                    binding.etRationCard.hint = "Ration Shop Number"
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val rationCard = binding.etRationCard.text.toString()
            val mobile = binding.etMobile.text.toString()
            val address = binding.etAddress.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etRegPassword.text.toString()
            
            // Determine role based on selected radio button
            val role = when (binding.rgRole.checkedRadioButtonId) {
                R.id.rbShopkeeper -> "SHOPKEEPER"
                R.id.rbAdmin -> "ADMIN"
                else -> "CUSTOMER"
            }

            if (name.isNotEmpty() && rationCard.isNotEmpty() && mobile.isNotEmpty() &&
                address.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                
                val user = User(
                    name = name,
                    rationCardNumber = rationCard,
                    mobileNumber = mobile,
                    address = address,
                    email = email,
                    role = role
                )

                viewModel.register(email, password, user) { success, error ->
                    if (success) {
                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
                    } else {
                        Toast.makeText(context, "Registration failed: $error", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
