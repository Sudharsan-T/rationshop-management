package com.example.meh

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.meh.data.RationViewModel
import com.example.meh.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    private var selectedRole = "customer"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRoleSelection()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false
            viewModel.login(email, password) { success, error ->
                binding.btnLogin.isEnabled = true
                if (success) {
                    Toast.makeText(context, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                    if (findNavController().currentDestination?.id == R.id.LoginFragment) {
                        findNavController().navigate(R.id.action_LoginFragment_to_DashboardFragment)
                    }
                } else {
                    Toast.makeText(context, getString(R.string.login_failed, error), Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }
    }

    private fun setupRoleSelection() {
        binding.rgRole.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCustomer -> selectedRole = "customer"
                R.id.rbShopkeeper -> selectedRole = "shopkeeper"
                R.id.rbAdmin -> selectedRole = "admin"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
