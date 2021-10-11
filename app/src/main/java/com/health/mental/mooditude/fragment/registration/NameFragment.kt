package com.health.mental.mooditude.fragment.registration

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.EmailRegistrationActivity
import com.health.mental.mooditude.databinding.FragmentNameBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.validateEmail


/**
 * A simple [Fragment] subclass.
 * Use the [NameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NameFragment : BaseFragment(), TextWatcher {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNameBinding.inflate(inflater, container, false)
        val view = binding.root

        //First reset error text
        binding.tvErrorInfo.text = ""

        binding.btnSignup.setOnClickListener {
            activity?.let { it1 -> UiUtils.hideKeyboard(it1) }
            //Let's first validate all texts
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            binding.tvErrorInfo.text = ""

            if (validateEmail(email)) {
                //Email validated, now check for password
                    if(password.isEmpty()) {
                        binding.tvErrorInfo.text = getString(R.string.please_enter_password)
                        binding.etPassword.requestFocus()
                    }
                else {
                        //printLog("OnSignup : " + mPhotoUri)
                        (activity as EmailRegistrationActivity).onSignupBtnClicked(
                            name,
                            email,
                            password
                        )
                    }
            } else {
                binding.tvErrorInfo.text = getString(R.string.invalid_email)
                binding.etEmail.requestFocus()
            }
            UiUtils.hideKeyboard(requireActivity())
        }

        setSignupEnabled(false)
        initializeEditText(binding.etName, this)
        initializeEditText(binding.etEmail, this)
        initializeEditText(binding.etPassword, this)

        binding.btnLogin.setOnClickListener {
            (activity as EmailRegistrationActivity).onLoginButtonClicked()
        }

        //editor event listener
        binding.etPassword.setOnEditorActionListener { v, actionId, event ->
            if (event == null || event.action == MotionEvent.ACTION_UP) {
                if (binding.btnSignup.isEnabled) {
                    binding.btnSignup.callOnClick()
                }
            }
            return@setOnEditorActionListener true
        }

        //clear button handling on touch
        addTouchListener(binding.etName)
        addTouchListener(binding.etEmail)
        addTouchListener(binding.etPassword)

        UiUtils.showKeyboard(this.requireActivity())

        //html text
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvTerms.setText(Html.fromHtml(getString(R.string.terms_and_conditions), 0))
        } else {
            binding.tvTerms.setText(Html.fromHtml(getString(R.string.terms_and_conditions)))
        }
        binding.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        return view
    }

    /**
     * Enables/disables NEXT button
     */
    fun setSignupEnabled(flag: Boolean) {
        binding.btnSignup.isEnabled = flag
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //PromoCode is optional
        binding.tvErrorInfo.text = ""
        if(binding.etName.text!!.trim().length == 0 ||
            binding.etEmail.text!!.trim().length == 0 ||
            binding.etPassword.text!!.trim().length == 0 ) {
            setSignupEnabled(false)
        }
        else {
            setSignupEnabled(true)
        }
        updateClearButton(binding.etName, binding.etName.text!!)
        updateClearButton(binding.etEmail, binding.etEmail.text!!)
        updateClearButton(binding.etPassword, binding.etPassword.text!!)
    }

}