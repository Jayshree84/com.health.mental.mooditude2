package com.health.mental.mooditude.fragment.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.HttpsCallableResult
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.activity.EmailRegistrationActivity
import com.health.mental.mooditude.cloudfunction.FBFunctionsHelper
import com.health.mental.mooditude.databinding.FragmentEmailBinding
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.utils.*
import org.jetbrains.anko.indeterminateProgressDialog
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [EmailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmailFragment : BaseFragment(), TextWatcher {


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        val view = binding.root

        //First reset error text
        binding.tvErrorInfo.text = ""

        binding.btnDontHaveCode.setOnClickListener {
            (activity as EmailRegistrationActivity).onDontHaveCodeBtnClicked()
        }

        binding.btnConfirm.setOnClickListener {

            if (activity is EmailRegistrationActivity) {

                //Let's first validate all texts
                val promoCode = binding.etPromoCode.text.toString()
                binding.tvErrorInfo.text = ""
                UiUtils.hideKeyboard(requireActivity())

                if (validatePromoCode(promoCode)) {

                    //Code validated,
                    val progressDlg =
                        requireContext().indeterminateProgressDialog(requireContext().getString(R.string.validating_code))
                    FBFunctionsHelper.instance.processInvitationCode(promoCode,
                        listener = object : FBQueryCompletedListener {
                            override fun onResultReceived(result: Any?) {
                                progressDlg.dismiss()
                                val task = result as Task<HttpsCallableResult>
                                if (!task.isSuccessful) {
                                    val e = task.exception
                                    binding.tvErrorInfo.text = e!!.localizedMessage
                                    errorLog(TAG, "Task exception : " + e.localizedMessage)
                                    if (e is FirebaseFunctionsException) {
                                        val code = e.code
                                        val details = e.details

                                        errorLog(TAG, "Error in code validation : " + code + " : " + details)
                                        binding.tvErrorInfo.text = e.localizedMessage
                                    }
                                } else {
                                    //task successfull
                                    //now check for body
                                    if (task.result != null) {
                                        val resultData = task.result

                                        val map = task.result!!.data as HashMap<String, Any>

                                        //check if error is present
                                        if (map.containsKey("error")) {
                                            val error = map.get("error") as HashMap<String, Any>
                                            val msg = error.get("message")
                                            if (msg != null && msg is String) {
                                                errorLog(TAG, "Error in code validation : " + error.toString() + " : " + msg)
                                                binding.tvErrorInfo.text = msg
                                            }
                                        } else {
                                            //success
                                            (requireActivity() as BaseActivity).invitationCodeProccessed(
                                                promoCode
                                            )
                                        }

                                    }
                                }
                            }
                        })
                } else {
                    binding.tvErrorInfo.text = getString(R.string.invalid_code)
                    binding.etPromoCode.requestFocus()
                }
            }
        }

        setConfirmEnabled(false)
        initializeEditText(binding.etPromoCode, this)


        //editor event listener
        binding.etPromoCode.setOnEditorActionListener { v, actionId, event ->
            if (event == null || event.action == MotionEvent.ACTION_UP) {
                if (binding.btnConfirm.isEnabled) {
                    binding.btnConfirm.callOnClick()
                }
            }
            return@setOnEditorActionListener true
        }


        //clear button handling on touch
        //addTouchListener(binding.etName)
        addTouchListener(binding.etPromoCode)
        UiUtils.showKeyboard(this.requireActivity())
        return view
    }

    /**
     * Enables/disables NEXT button
     */
    fun setConfirmEnabled(flag: Boolean) {
        binding.btnConfirm.isEnabled = flag
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //Reset error
        binding.tvErrorInfo.text = ""

        if (binding.etPromoCode.text!!.trim().length < 4) {
            setConfirmEnabled(false)
        } else {
            setConfirmEnabled(true)
        }

        //check length and show/hide clear button
        updateClearButton(binding.etPromoCode, binding.etPromoCode.text!!)
    }


}