package space.mrandika.dicogram.view.auth

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
import space.mrandika.dicogram.databinding.FragmentRegisterBinding
import space.mrandika.dicogram.viewmodel.auth.RegisterViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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

        viewModel.response.observe(viewLifecycleOwner) {
            setResponse()
        }

        binding?.registerLoading?.pbLoading?.visibility = View.GONE
        binding?.btnRegister?.isEnabled = false
        addTextChangeListener()

        binding?.btnRegister?.setOnClickListener {
            val name = binding?.etName?.text.toString()
            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()

            lifecycleScope.launch {
                viewModel.register(name, email, password)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            btnRegister.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            registerLoading.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showError() {
        Toast.makeText(activity, getString(R.string.register_failed), Toast.LENGTH_LONG).show()
    }

    private fun setResponse() {
        Toast.makeText(activity, getString(R.string.register_success), Toast.LENGTH_LONG).show()

        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    private fun addTextChangeListener() {
        binding?.etEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkData(
                    binding?.etEmail?.error,
                    binding?.etPassword?.error,
                    binding?.etPasswordConfirm?.error
                )
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
                checkData(
                    binding?.etEmail?.error,
                    binding?.etPassword?.error,
                    binding?.etPasswordConfirm?.error
                )
            }

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }
        })

        binding?.etPasswordConfirm?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != binding?.etPassword?.text.toString()) {
                    binding?.fieldPasswordConfirm?.error =
                        getString(R.string.error_password_notmatch)
                } else {
                    binding?.fieldPasswordConfirm?.error = null
                }

                checkData(
                    binding?.etEmail?.error,
                    binding?.etPassword?.error,
                    binding?.etPasswordConfirm?.error
                )
            }

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }
        })
    }

    private fun checkValidator(value: Boolean) {
        binding?.btnRegister?.isEnabled = value
    }

    private fun checkData(
        emailError: CharSequence?,
        passwordError: CharSequence?,
        passwordConfirmError: CharSequence?
    ) {
        binding?.apply {
            viewModel._isValidated.value = (!etEmail.text.isNullOrEmpty() && emailError == null)
                    && (!etPassword.text.isNullOrEmpty() && passwordError == null)
                    && (!etPasswordConfirm.text.isNullOrEmpty() && passwordConfirmError == null)
        }
    }
}