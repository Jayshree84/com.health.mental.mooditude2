package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.TherapistRequestInfo
import com.health.mental.mooditude.data.model.Veteran
import com.health.mental.mooditude.databinding.FragmentUserPersonalInfoBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils

/**
 * A simple [Fragment] subclass.
 * Use the [UserPersonalInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserPersonalInfoFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentUserPersonalInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserPersonalInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val user = DataHolder.instance.getCurrentUser()!!

        //name
        binding.etName.setText(user.name)

        //gender
        val genderArray = arrayOf(getString(R.string.select_gender),
                                    getString(R.string.gender_1),
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
        binding.spinnerGender.setSelection(user.gender)

        //age
        val ageArray = arrayOf(getString(R.string.select_ur_age), getString(R.string.agegroup_1),
            getString(R.string.agegroup_2), getString(R.string.agegroup_3), getString(R.string.agegroup_4), getString(R.string.agegroup_5))
        val adapterAge: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, ageArray
        )

        adapterAge.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerAge.setAdapter(adapterAge)
        binding.spinnerAge.setSelection(user.ageGroup)

        //states
        val stateArray = requireContext().resources.getStringArray(R.array.usa_states)
        val adapterState: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, stateArray
        )

        adapterState.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerState.setAdapter(adapterState)
        val position = stateArray.indexOf(user.state)
        if(position != -1) {
            binding.spinnerState.setSelection(position)
        }

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

        return root
    }

    fun updateUserAttributes(requestInfo: TherapistRequestInfo) : Boolean {

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
        if(genderPos == 0) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_gender))
            binding.spinnerGender.requestFocus()
            return success
        }

        //check age
        val agePos = binding.spinnerAge.selectedItemPosition
        if(agePos == 0) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_ur_age))
            binding.spinnerAge.requestFocus()
            return success
        }

        //check state
        val statePos = binding.spinnerState.selectedItemPosition
        if(statePos == 0) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_state))
            binding.spinnerState.requestFocus()
            return success
        }

        //check state
        val veteranPos = binding.spinnerVeteran.selectedItemPosition
        if(veteranPos == 0) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.select_veteran_status1))
            binding.spinnerVeteran.requestFocus()
            return success
        }

        //update name
        DBManager.instance.updateUserName(textName)
        requestInfo.name = textName

        //gender
        var selection = binding.spinnerGender.selectedItemPosition
        DBManager.instance.updateGender(selection)
        requestInfo.gender = binding.spinnerGender.selectedItem.toString()

        //age
        selection = binding.spinnerAge.selectedItemPosition
        DBManager.instance.updateUserAgeGroup(selection)
        requestInfo.ageGroup = binding.spinnerAge.selectedItem.toString()

        //state
        val state = binding.spinnerState.selectedItem.toString()
        DBManager.instance.updateUserState(state)
        requestInfo.state = state

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
        requestInfo.veteranStatus = veteranVal.getLocalizedString(requireContext())
        DBManager.instance.updateUserVeteran(veteranVal)

        success = true
        return success
    }

}