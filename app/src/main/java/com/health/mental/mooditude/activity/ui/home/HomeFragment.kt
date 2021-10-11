package com.health.mental.mooditude.activity.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.FragmentHomeBinding
import com.health.mental.mooditude.fragment.BaseFragment

class HomeFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //add fragment
        val fragment = MainFragment()
        addFragment(R.id.layout_container, fragment, true)

        return root
    }

    fun updateUserDetails() {
        val fragment = childFragmentManager.findFragmentById(R.id.layout_container)
        if (fragment is MainFragment) {
            (fragment as MainFragment).updateUserDetails()
        }
    }

}