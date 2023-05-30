package space.mrandika.dicogram.view.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import space.mrandika.dicogram.R
import space.mrandika.dicogram.data.model.remote.LoginResponse
import space.mrandika.dicogram.databinding.FragmentLoginBinding
import space.mrandika.dicogram.view.RootActivity
import space.mrandika.dicogram.viewmodel.auth.LoginViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar? = binding?.toolbar
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            showError()
        }

        viewModel.isValidated.observe(viewLifecycleOwner) {
            checkValidator(it)
        }

        viewModel.response.observe(viewLifecycleOwner) { response ->
            setUserData(response)
        }

        binding?.loginLoading?.pbLoading?.visibility = View.GONE
        binding?.btnLogin?.isEnabled = false
        addTextChangeListener()

        binding?.btnLogin?.setOnClickListener {
            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()

            lifecycleScope.launch {
                viewModel.login(email, password)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            btnLogin.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            loginLoading.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showError() {
        Toast.makeText(activity, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
    }

    private fun setUserData(response: LoginResponse) {
        Toast.makeText(activity, "Hi ${response.user?.name}", Toast.LENGTH_SHORT).show()

        val intent = Intent(activity, RootActivity::class.java)
        startActivity(intent)
    }

    private fun addTextChangeListener() {
        binding?.etEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkData(binding?.etEmail?.error, binding?.etPassword?.error)
            }

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }
        })

        binding?.etPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkData(binding?.etEmail?.error, binding?.etPassword?.error)
            }

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }
        })
    }

    private fun checkValidator(value: Boolean) {
        binding?.btnLogin?.isEnabled = value
    }

    private fun checkData(emailError: CharSequence?, passwordError: CharSequence?) {
        binding?.apply {
            viewModel._isValidated.value = (!etEmail.text.isNullOrEmpty() && emailError == null)
                    && (!etPassword.text.isNullOrEmpty() && passwordError == null)
        }
    }
}