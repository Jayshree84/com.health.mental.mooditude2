package com.health.mental.mooditude.fragment.preferences

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.databinding.FragmentDeleteAccountBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [DeleteAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteAccountFragment() : BaseFragment(), TextWatcher {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentDeleteAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createViews()
        return root
    }

    private fun createViews() {
        //First reset error text
        binding.tvErrorInfo.text = ""

        binding.btnDelete.setOnClickListener {
            //If confirmed
            val password = binding.etPassword.text.toString()
            binding.tvErrorInfo.text = ""
            if (password.isEmpty()) {
                binding.tvErrorInfo.text = getString(R.string.please_enter_password)
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }
            UiUtils.hideKeyboard(requireActivity())

            //show alert
            showConfirmationDlg()
        }

        setDeleteBtnEnabled(false)
        initializeEditText(binding.etPassword, this)

        //editor event listener
        binding.etPassword.setOnEditorActionListener { v, actionId, event ->
            if (event == null || event.action == MotionEvent.ACTION_UP) {
                if (binding.btnDelete.isEnabled) {
                    binding.btnDelete.callOnClick()
                }
            }
            return@setOnEditorActionListener true
        }

        //clear button handling on touch
        addTouchListener(binding.etPassword)

        UiUtils.showKeyboard(this.requireActivity())
    }

    /**
     * Enables/disables NEXT button
     */
    fun setDeleteBtnEnabled(flag: Boolean) {
        //binding.btnDelete.isEnabled = flag
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //PromoCode is optional
        binding.tvErrorInfo.text = ""
        if (binding.etPassword.text!!.trim().length == 0) {
            setDeleteBtnEnabled(false)
        } else {
            setDeleteBtnEnabled(true)
        }
        updateClearButton(binding.etPassword, binding.etPassword.text!!)
    }

    private fun showConfirmationDlg() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_delete_account, null)
        dialogBuilder.setView(dialogView)

        val alertDialog: AlertDialog = dialogBuilder.create()
        dialogView.findViewById<View>(R.id.btn_delete2).setOnClickListener {
            debugLog(TAG, "Delete account now")
            (requireActivity() as BaseActivity).deleteAccount(binding.etPassword.text.toString())

            alertDialog.dismiss()
        }

        alertDialog.show()
    }


}