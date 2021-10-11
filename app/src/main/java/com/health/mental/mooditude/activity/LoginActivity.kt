package com.health.mental.mooditude.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.ActivityLoginBinding
import com.health.mental.mooditude.utils.validateEmail

class LoginActivity : RegistrationActivity(), TextWatcher {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActionBar(findViewById(R.id.toolbar), getString(R.string.login))
        initComponents()
    }

    override fun showError(exception: Exception) {
        TODO("Not yet implemented")
    }

    override fun initComponents() {
        binding.btnSignin.setOnClickListener {
            //Let's first validate all texts
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            binding.emailLayout.error = ""
            binding.passwordLayout.error = ""
            if (validateEmail(email)) {
                //Email validated, now check for password
                if (password.isNotEmpty()) {
                    val credential = EmailAuthProvider.getCredential(email, password) as EmailAuthCredential
                    signInWithEmailAuthProvider(credential)
                }
            } else {
                binding.emailLayout.error = getString(R.string.invalid_email)
            }
        }

        setLoginEnabled(false)
        initializeEditText(binding.etEmail, this)
        initializeEditText(binding.etPassword, this)

        //editor event listener
        binding.etPassword.setOnEditorActionListener { v, actionId, event ->
            if (event == null || event.action == MotionEvent.ACTION_UP) {
                if (binding.btnSignin.isEnabled) {
                    binding.btnSignin.callOnClick()
                }
            }
            return@setOnEditorActionListener true
        }
    }

    /**
     * Enables/disables NEXT button
     */
    fun setLoginEnabled(flag: Boolean) {
        binding.btnSignin.isEnabled = flag
    }

    /**
     * Performs common API calls for phone registration
     */
    protected fun initializeEditText(editText: EditText, textWatcher: TextWatcher) {

        //show keyboard
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.showSoftInput(editText, 0)

        //add textwatcher
        editText.addTextChangedListener(textWatcher)
        //editText.setOnEditorActionListener(editorEventListener)

        addTouchListener(editText)
    }

    /**
     * Adds touch listener to handle touchevent on clear button
     */
    protected fun addTouchListener(editText: EditText) {
        editText.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (motionEvent != null && motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (motionEvent.x >= (view.width - (view as EditText).compoundPaddingRight)) {
                    clearText(view)
                }
            }
            return@OnTouchListener false
        })
    }

    protected fun clearText(view:EditText) {
        view.setText("")

        //show keyboard
        view.requestFocus()
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //Reset error
        binding.emailLayout.error = ""
        /* UiUtils.setBackgroundTint(binding.etEmail, ColorStateList.valueOf(
             ContextCompat.getColor(
                 requireActivity(),
             R.color.boarding_text_grey)))*/

        if(binding.etEmail.text!!.trim().length == 0 ||
            binding.etPassword.text!!.trim().length == 0 ) {
            setLoginEnabled(false)
        }
        else {
            setLoginEnabled(true)
        }

        //check length and show/hide clear button
        updateClearButton(binding.etEmail, binding.etEmail.text!!)
        updateClearButton(binding.etPassword, binding.etPassword.text!!)
    }

    /**
     * Show/hides clear button
     */
    protected fun updateClearButton(editText: EditText, s: CharSequence) {
        if (s.length > 0) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(this, R.drawable.ic_clear), null)
        } else {
            editText.setCompoundDrawables(null, null, null, null)
        }
    }
}