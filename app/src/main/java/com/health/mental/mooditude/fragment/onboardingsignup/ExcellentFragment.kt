package com.health.mental.mooditude.fragment.onboardingsignup

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.OnBoardingActivity
import com.health.mental.mooditude.databinding.*
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [ExcellentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExcellentFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentExcellentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExcellentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnContinue.setOnClickListener {
            (requireActivity() as OnBoardingActivity).onExcellentContinueBtnClicked()
        }

        binding.btnSkip.setOnClickListener {
            (requireActivity() as OnBoardingActivity).onSignupBtnClicked()
        }

        return view
    }
}