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
 * Use the [CBTFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CBTFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentProfileSettingsBinding

    private var mSelectedOption: Boolean? = null
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
        binding.tvTitle.text = getString(R.string.CBT_title)
        binding.tvDesc.text = ""

        binding.btnContinue.setOnClickListener {
            if (activity is SetUserProfileActivity) {
                if (mSelectedOption != null) {
                    (activity as SetUserProfileActivity).onCBTSelected(mSelectedOption!!)
                }
            }
        }

        setNextEnabled(false)

        //listview
        val recyclerLayoutManager = LinearLayoutManager(requireActivity())
        binding.listEntries.setLayoutManager(recyclerLayoutManager)

        //Only for below <18 description text is available
        mListOptions = arrayListOf<ProfileOption>(
            ProfileOption(getString(R.string.cbt_yes), getString(R.string.cbt_yes_desc), true),
            ProfileOption(getString(R.string.cbt_no), getString(R.string.cbt_no_desc), false),
        )
        val adapter = ProfileOptionAdapter(mListOptions, requireActivity(), object : OptionSelectListener {
            override fun onOptionSelected(position: Int) {
                if (position >= 0) {
                    setNextEnabled(true)
                    mSelectedOption = mListOptions.get(position).value as Boolean
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