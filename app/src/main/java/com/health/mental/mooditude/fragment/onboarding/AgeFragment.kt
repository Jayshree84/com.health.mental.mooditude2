package com.health.mental.mooditude.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.SetUserProfileActivity
import com.health.mental.mooditude.adapter.ProfileOptionAdapter
import com.health.mental.mooditude.databinding.FragmentProfileSettingsBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.listener.OptionSelectListener
import com.health.mental.mooditude.model.ProfileOption


/**
 * A simple [Fragment] subclass.
 * Use the [AgeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgeFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentProfileSettingsBinding

    private var mSelectedOption: ProfileOption? = null
    private var mListOptions = ArrayList<ProfileOption>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        //First reset error text
        binding.tvTitle.text = getString(R.string.age_title)
        binding.tvDesc.text = ""

        binding.btnContinue.setOnClickListener {
            if (activity is SetUserProfileActivity) {
                if (mSelectedOption != null) {
                    (activity as SetUserProfileActivity).onAgeSelected(mSelectedOption!!.value)
                }
            }
        }

        setNextEnabled(false)

        //listview
        val recyclerLayoutManager = LinearLayoutManager(requireActivity())
        binding.listEntries.setLayoutManager(recyclerLayoutManager)

        //Only for below <18 description text is available
        mListOptions = arrayListOf<ProfileOption>(
            ProfileOption(getString(R.string.agegroup_1), getString(R.string.below_age_desc), 1),
            ProfileOption(getString(R.string.agegroup_2), "", 2),
            ProfileOption(getString(R.string.agegroup_3), "", 3),
            ProfileOption(getString(R.string.agegroup_4), "", 4),
            ProfileOption(getString(R.string.agegroup_5), "", 5)
        )
        val adapter = ProfileOptionAdapter(mListOptions, requireActivity(), object : OptionSelectListener {
            override fun onOptionSelected(position: Int) {
                if (position >= 0) {
                    setNextEnabled(true)
                    mSelectedOption = mListOptions.get(position)
                    if(position == 0) {
                        binding.btnContinue.text = getString(R.string.i_have_consent)
                    }
                    else {
                        binding.btnContinue.text = getString(R.string.continue_text)
                    }
                } else {
                    setNextEnabled(false)
                    mSelectedOption = null
                }
            }

        })
        binding.listEntries.adapter = adapter
        return view
    }

    /**
     * Enables/disables NEXT button
     */
    fun setNextEnabled(flag: Boolean) {
        binding.btnContinue.isEnabled = flag
    }
}