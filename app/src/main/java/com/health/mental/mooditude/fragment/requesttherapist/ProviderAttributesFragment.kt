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
import com.health.mental.mooditude.data.model.Ethnicity
import com.health.mental.mooditude.data.model.TherapistProviderAttributes
import com.health.mental.mooditude.databinding.FragmentProviderAttributesBinding
import com.health.mental.mooditude.fragment.BaseFragment

/**
 * A simple [Fragment] subclass.
 * Use the [ProviderAttributesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProviderAttributesFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentProviderAttributesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProviderAttributesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val user = DataHolder.instance.getCurrentUser()!!
        val providerAttributes = user.providerAttributes

        //gender
        val genderArray = arrayOf(getString(R.string.doesnt_matter), getString(R.string.gender_1),
            getString(R.string.gender_2), getString(R.string.gender_3), getString(R.string.gender_4), getString(R.string.gender_5))

        val adapterGender: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, genderArray
        )

        adapterGender.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerGender.setAdapter(adapterGender)
        var index = genderArray.indexOf(providerAttributes.gender)
        if(index != -1) {
            binding.spinnerGender.setSelection(index)
        }

        //age
        val ageArray = arrayOf(getString(R.string.doesnt_matter), getString(R.string.agegroup_1),
            getString(R.string.agegroup_2), getString(R.string.agegroup_3), getString(R.string.agegroup_4), getString(R.string.agegroup_5))
        val adapterAge: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, ageArray
        )

        adapterAge.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerAge.setAdapter(adapterAge)
        index = ageArray.indexOf(providerAttributes.ageGroup)
        if(index != -1) {
            binding.spinnerAge.setSelection(index)
        }

        //ethnicity
        val ethnicityArray = Ethnicity.getArray(requireContext())
        val adapterEthnicity: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.select_dialog_singlechoice, ethnicityArray
        )

        adapterEthnicity.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerEthnicity.setAdapter(adapterEthnicity)
        val position1 = ethnicityArray.indexOf(providerAttributes.ethnicity)
        if(position1 != -1) {
            binding.spinnerEthnicity.setSelection(position1)
        }

        binding.etLanguage.setText(providerAttributes.language)
        binding.etReligion.setText(providerAttributes.religion)
        return root
    }

    fun getTherapistProviderAttributes() : TherapistProviderAttributes {
        val attributes = TherapistProviderAttributes()
        attributes.ageGroup = binding.spinnerAge.selectedItem.toString()
        attributes.gender = binding.spinnerGender.selectedItem.toString()

        /*val position = binding.spinnerEthnicity.selectedItemPosition
        var selectedVal = Ethnicity.unknown
        when(position) {
            0 -> {
                selectedVal = Ethnicity.unknown
            }
            1 -> {
                selectedVal = Ethnicity.caucasian
            }
            2 -> {
                selectedVal = Ethnicity.hispanic
            }
            3 -> {
                selectedVal = Ethnicity.africanAmerican
            }
            4 -> {
                selectedVal = Ethnicity.southAsian
            }
            5 -> {
                selectedVal = Ethnicity.eastAsian
            }
            6 -> {
                selectedVal = Ethnicity.caribbean
            }
            7 -> {
                selectedVal = Ethnicity.biOrMultiracial
            }
            8 -> {
                selectedVal = Ethnicity.other
            }
        }*/

        //update value
        attributes.ethnicity = binding.spinnerEthnicity.selectedItem.toString()

        attributes.language = binding.etLanguage.text.toString()
        attributes.religion = binding.etReligion.text.toString()

        DBManager.instance.updateUserProviderAttributes(attributes)

        return attributes
    }

}