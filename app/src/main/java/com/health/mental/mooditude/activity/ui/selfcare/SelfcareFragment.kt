package com.health.mental.mooditude.activity.ui.selfcare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.databinding.FragmentSelfcareBinding
import com.health.mental.mooditude.fragment.BaseFragment

class SelfcareFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentSelfcareBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfcareBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //add fragment
        val fragment = MainFragment()
        addFragment(R.id.layout_container, fragment, true)

        binding.fab.setOnClickListener {
            (requireActivity() as BaseActivity).addNewJournalEntry()
        }
        return root
    }
}
