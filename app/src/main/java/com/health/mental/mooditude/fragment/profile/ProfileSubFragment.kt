package com.health.mental.mooditude.fragment.profile

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.Veteran
import com.health.mental.mooditude.databinding.FragmentProfileSubBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.validatePhoneNumber

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileSubFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileSubFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentProfileSubBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileSubBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val user = DataHolder.instance.getCurrentUser()!!

        //name
        binding.etName.setText(user.name)

        //gender
        val genderArray = arrayOf(  getString(R.string.gender_1),
                                    getString(R.string.gender_2),
                                    getString(R.string.gender_3),
                                    getString(R.string.gender_4),
                                    getString(R.string.gender_5))

        val adapterGender: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, genderArray
        )

        adapterGender.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerGender.setAdapter(adapterGender)
        binding.spinnerGender.setSelection(user.gender-1)

        //age
        val ageArray = arrayOf(getString(R.string.agegroup_1),
            getString(R.string.agegroup_2), getString(R.string.agegroup_3), getString(R.string.agegroup_4), getString(R.string.agegroup_5))
        val adapterAge: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, ageArray
        )

        adapterAge.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerAge.setAdapter(adapterAge)
        binding.spinnerAge.setSelection(user.ageGroup-1)

        //Veteran
        val veteranArray = Veteran.getArray(requireContext())
        val adapterVeteran: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, veteranArray
        )

        adapterVeteran.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerVeteran.setAdapter(adapterVeteran)
        val position1 = veteranArray.indexOf(user.veteranStatus.getLocalizedString(requireContext()))
        if(position1 != -1) {
            binding.spinnerVeteran.setSelection(position1)
        }

        binding.etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))

        //set phone
        if(user.phone != null && user.phone!!.trim().isNotEmpty()) {
            binding.etPhone.setText(user.phone!!.removePrefix(binding.tvPrefix.text))
        }

        UiUtils.hideKeyboard(requireActivity())
        return root
    }

    fun updateUserAttributes() : Boolean {

        var success = false
        //Let's first check for name
        val textName = binding.etName.text.toString()
        if(textName.trim().isEmpty()) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.please_enter_name))
            binding.etName.requestFocus()
            return success
        }
        //check gender
        val genderPos = binding.spinnerGender.selectedItemPosition
        if(genderPos == Spinner.INVALID_POSITION) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_gender))
            binding.spinnerGender.requestFocus()
            return success
        }

        //check age
        val agePos = binding.spinnerAge.selectedItemPosition
        if(agePos == Spinner.INVALID_POSITION) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_ur_age))
            binding.spinnerAge.requestFocus()
            return success
        }

        //check state
        val veteranPos = binding.spinnerVeteran.selectedItemPosition
        if(veteranPos == 0) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_veteran_status1))
            binding.spinnerVeteran.requestFocus()
            return success
        }

        //Phone number
        var textPhone = binding.etPhone.text.toString()
        textPhone = PhoneNumberUtils.normalizeNumber(textPhone)
        if(textPhone.isNotEmpty() && !validatePhoneNumber(textPhone)) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.invalid_phone))
            binding.etPhone.requestFocus()
            return success
        }

        //update name
        DBManager.instance.updateUserName(textName)

        //gender
        var selection = binding.spinnerGender.selectedItemPosition + 1
        DBManager.instance.updateGender(selection)

        //age
        selection = binding.spinnerAge.selectedItemPosition + 1
        DBManager.instance.updateUserAgeGroup(selection)

        //veteran
        val position = binding.spinnerVeteran.selectedItemPosition
        var veteranVal = Veteran.unknown
        when(position) {
            0 -> {
                veteranVal = Veteran.unknown
            }
            1 -> {
                veteranVal = Veteran.notVeteran
            }
            2 -> {
                veteranVal = Veteran.postNineEleven
            }
            3 -> {
                veteranVal = Veteran.preNineEleven
            }
        }
        DBManager.instance.updateUserVeteran(veteranVal)

        //update phone number
        DBManager.instance.updateUserPhone(binding.tvPrefix.text.toString() + textPhone)

        success = true
        return success
    }

}