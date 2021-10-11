package com.health.mental.mooditude.fragment.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.SetUserProfileActivity
import com.health.mental.mooditude.adapter.ProfileGroupOptionAdapter
import com.health.mental.mooditude.data.model.UserChallenge
import com.health.mental.mooditude.databinding.FragmentProfileSettingsBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.listener.GroupOptionSelectListener
import com.health.mental.mooditude.model.ProfileOption

/**
 * A simple [Fragment] subclass.
 * Use the [ChallengesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChallengesFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentProfileSettingsBinding

    private var mSelectedOptions = ArrayList<ProfileOption>()
    private var mListOptions = ArrayList<ProfileOption>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        //First reset error text
        binding.tvTitle.text = getString(R.string.challenges_title)
        binding.tvDesc.text = getString(R.string.challenges_desc)

        binding.btnContinue.setOnClickListener {
            if (activity is SetUserProfileActivity) {
                if (mSelectedOptions.size > 0) {
                    var commaSeperatedText = ""
                    for (option in mSelectedOptions) {
                        commaSeperatedText = commaSeperatedText.plus((option.value as UserChallenge).toString())
                            .plus(",")
                    }
                    (activity as SetUserProfileActivity).onChallengesSelected(
                        commaSeperatedText.trim(
                            ','
                        )
                    )
                }
            }
        }

        setNextEnabled(false)

        //listview
        val recyclerLayoutManager = LinearLayoutManager(requireActivity())
        binding.listEntries.setLayoutManager(recyclerLayoutManager)

        //Only for below <18 description text is available
        mListOptions = arrayListOf<ProfileOption>(
            ProfileOption(getString(R.string.challenge_people), "", UserChallenge.people),
            ProfileOption(getString(R.string.challenge_work), "", UserChallenge.work),
            ProfileOption(getString(R.string.challenge_health), "", UserChallenge.health),
            ProfileOption(getString(R.string.challenge_money), "", UserChallenge.money),
            ProfileOption(getString(R.string.challenge_me), "", UserChallenge.me)
        )

        val adapter = ProfileGroupOptionAdapter(mListOptions, requireActivity(),
            object : GroupOptionSelectListener {
                override fun onGroupOptionsSelected(list: ArrayList<Int>) {
                    mSelectedOptions.clear()
                    for (value in list) {
                        mSelectedOptions.add(mListOptions.get(value))
                    }
                    if(mSelectedOptions.size > 0) {
                        setNextEnabled(true)
                    }
                    else {
                        setNextEnabled(false)
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