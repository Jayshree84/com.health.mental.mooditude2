package com.health.mental.mooditude.activity.ui.care

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.FragmentNoStateOperatingBinding
import com.health.mental.mooditude.fragment.BaseFragment

/**
 * A simple [Fragment] subclass.
 * Use the [NoStateOperatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoStateOperatingFragment(val mSelectedState: String) : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentNoStateOperatingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoStateOperatingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvTitle.text = String.format(getString(R.string.we_are_not_in_state), mSelectedState)
        return root
    }



}