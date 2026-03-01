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
import com.example.meh.databinding.FragmentLoginBinding

/**
 * Fragment responsible for handling User Login.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Show a simple loading indicator (optional but recommended)
                binding.btnLogin.isEnabled = false
                
                viewModel.login(email, password) { success, error ->
                    binding.btnLogin.isEnabled = true
                    if (success) {
                        Toast.makeText(context, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                        // Immediate navigation once profile is ready
                        if (findNavController().currentDestination?.id == R.id.LoginFragment) {
                            findNavController().navigate(R.id.action_LoginFragment_to_DashboardFragment)
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.login_failed, error), Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
