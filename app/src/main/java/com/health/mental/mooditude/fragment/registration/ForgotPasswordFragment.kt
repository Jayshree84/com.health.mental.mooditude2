package com.health.mental.mooditude.fragment.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.EmailRegistrationActivity
import com.health.mental.mooditude.databinding.FragmentForgotPasswordBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.validateEmail

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordFragment : BaseFragment(), TextWatcher {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        //First reset error text
        binding.tvErrorInfo.text = ""

        //On login button
        binding.btnResetPwd.setOnClickListener {
            //Let's first validate all texts
            val email = binding.etEmail.text.toString()
            if (validateEmail(email)) {
                UiUtils.hideKeyboard(requireActivity())
                (requireActivity() as EmailRegistrationActivity).onForgotPassword(email)
            } else {
                binding.tvErrorInfo.text = getString(R.string.invalid_email)
                UiUtils.showKeyboard(requireActivity())
                binding.etEmail.requestFocus()
            }
        }

        setForgotPwdEnabled(false)
        initializeEditText(binding.etEmail, this)

        //editor event listener
        binding.etEmail.setOnEditorActionListener { v, actionId, event ->
            if (event == null || event.action == MotionEvent.ACTION_UP) {
                if (binding.btnResetPwd.isEnabled) {
                    binding.btnResetPwd.callOnClick()
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
    fun setForgotPwdEnabled(flag: Boolean) {
        binding.btnResetPwd.isEnabled = flag
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //Reset error
        binding.tvErrorInfo.text = ""

        if(binding.etEmail.text!!.trim().length == 0) {
            setForgotPwdEnabled(false)
        }
        else {
            setForgotPwdEnabled(true)
        }

        //check length and show/hide clear button
        updateClearButton(binding.etEmail, binding.etEmail.text!!)
    }


}