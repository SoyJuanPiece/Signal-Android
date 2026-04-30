package org.thoughtcrime.securesms.registration.ui.supabase

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.github.jan.tennert.supabase.gotrue.auth
import io.github.jan.tennert.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import org.thoughtcrime.securesms.R
import org.thoughtcrime.securesms.databinding.FragmentSupabaseLoginBinding
import org.thoughtcrime.securesms.util.SupabaseHelper

class SupabaseLoginFragment : Fragment(R.layout.fragment_supabase_login) {

    private var _binding: FragmentSupabaseLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSupabaseLoginBinding.bind(view)

        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                performLogin(email, password)
            } else {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerLink.setOnClickListener {
            // TODO: Implementar navegación a registro o lógica de switch
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                performSignUp(email, password)
            } else {
                Toast.makeText(requireContext(), "Rellena todos los campos para registrarte", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        binding.loginButton.setSpinning(true)
        lifecycleScope.launch {
            try {
                SupabaseHelper.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                Toast.makeText(requireContext(), "Login exitoso", Toast.LENGTH_SHORT).show()
                // TODO: Navegar al MainActivity de Signal
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.loginButton.setSpinning(false)
            }
        }
    }

    private fun performSignUp(email: String, password: String) {
        binding.loginButton.setSpinning(true)
        lifecycleScope.launch {
            try {
                SupabaseHelper.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                Toast.makeText(requireContext(), "Registro exitoso. Revisa tu email.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.loginButton.setSpinning(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
