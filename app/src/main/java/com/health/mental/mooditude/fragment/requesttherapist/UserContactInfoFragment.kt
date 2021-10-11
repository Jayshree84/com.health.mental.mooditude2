package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.*
import com.health.mental.mooditude.databinding.FragmentUserContactInfoBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.validateEmail
import com.health.mental.mooditude.utils.validatePhoneNumber

/**
 * A simple [Fragment] subclass.
 * Use the [UserContactInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserContactInfoFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentUserContactInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserContactInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val user = DataHolder.instance.getCurrentUser()!!

        //email
        binding.etEmail.setText(user.email)

        //contact time
        val timeArray = ContactTime.getArray(requireContext())

        val adapterTime: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, timeArray
        )

        adapterTime.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerTime.setAdapter(adapterTime)
        val position1 = timeArray.indexOf(user.bestTimeToContact.getLocalizedString(requireContext()))
        binding.spinnerTime.setSelection(position1)

        binding.etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))

        //set phone
        if(user.phone != null && user.phone!!.trim().isNotEmpty()) {
            binding.etPhone.setText(user.phone!!.removePrefix(binding.tvPrefix.text))
        }

        return root
    }

    fun updateContactInfo(requestInfo: TherapistRequestInfo) : Boolean {

        var success = false

        //Phone number
        var textPhone = binding.etPhone.text.toString()
        textPhone = PhoneNumberUtils.normalizeNumber(textPhone)
        if(!validatePhoneNumber(textPhone)) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.invalid_phone))
            binding.etPhone.requestFocus()
            return success
        }

        //Let's first check for email
        val textEmail = binding.etEmail.text.toString()

        if(textEmail.trim().isEmpty()) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.please_enter_email))
            binding.etEmail.requestFocus()
            return success
        }

        if(!validateEmail(textEmail)) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.invalid_email))
            binding.etEmail.requestFocus()
            return success
        }
        //update email
        requestInfo.email = textEmail

        //update phone number
        val fullPhone:String = binding.tvPrefix.text.toString() + textPhone
        DBManager.instance.updateUserPhone(fullPhone)
        requestInfo.phone = fullPhone

        //contact time
        val selection = binding.spinnerTime.selectedItemPosition

        var contactTime = ContactTime.morning
        when(selection) {
            0 -> {
                contactTime = ContactTime.morning
            }
            1 -> {
                contactTime = ContactTime.afternoon
            }
            2 -> {
                contactTime = ContactTime.evening
            }
        }
        requestInfo.bestTimeToContact = contactTime.getLocalizedString(requireContext())
        DBManager.instance.updateUserContactTime(contactTime)

        success = true
        return success
    }



}