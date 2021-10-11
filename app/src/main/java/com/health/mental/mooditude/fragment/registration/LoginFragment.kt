package com.health.mental.mooditude.fragment.registration

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.EmailRegistrationActivity
import com.health.mental.mooditude.databinding.FragmentLoginBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.validateEmail


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : BaseFragment(), TextWatcher {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        //First reset error text
        binding.tvErrorInfo.text = ""

        //On login button
        binding.btnLogin.setOnClickListener {
            //Let's first validate all texts
            debugLog(TAG, "btnLogin clicked")
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            binding.tvErrorInfo.text = ""
            UiUtils.hideKeyboard(requireActivity())
            if (validateEmail(email)) {
                //Email validated, now check for password
                if (password.isNotEmpty()) {
                    (requireActivity() as EmailRegistrationActivity).onLoginUser(email, password)
                }
            } else {
                binding.tvErrorInfo.text = getString(R.string.invalid_email)
                binding.etEmail.requestFocus()
            }
        }

        //On login button
        binding.btnForgotPassword.setOnClickListener {
            (requireActivity() as EmailRegistrationActivity).onForgotPwdClicked()
        }

        binding.btnSignup.setOnClickListener {
            (requireActivity() as EmailRegistrationActivity).onBackPressed()
        }

        setLoginEnabled(false)
        initializeEditText(binding.etEmail, this)
        initializeEditText(binding.etPassword, this)

        //editor event listener
        binding.etPassword.setOnEditorActionListener { v, actionId, event ->
            if (event == null || event.action == MotionEvent.ACTION_UP) {
                if (binding.btnLogin.isEnabled) {
                    binding.btnLogin.callOnClick()
                }
            }
            return@setOnEditorActionListener true
        }

        UiUtils.showKeyboard(this.requireActivity())
        return view
    }

    /**
     * Enables/disables NEXT button
     */
    fun setLoginEnabled(flag: Boolean) {
        binding.btnLogin.isEnabled = flag
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //Reset error
        binding.tvErrorInfo.text = ""

        if(binding.etEmail.text!!.trim().length == 0 ||
            binding.etPassword.text!!.trim().length == 0) {
            setLoginEnabled(false)
        }
        else {
            setLoginEnabled(true)
        }

        //check length and show/hide clear button
        updateClearButton(binding.etEmail, binding.etEmail.text!!)
        updateClearButton(binding.etPassword, binding.etPassword.text!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        UiUtils.showKeyboard(context)
    }

}