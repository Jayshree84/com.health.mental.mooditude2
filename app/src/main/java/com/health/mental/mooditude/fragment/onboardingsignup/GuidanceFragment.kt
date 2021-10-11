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
import com.health.mental.mooditude.databinding.FragmentDailyCheckinBinding
import com.health.mental.mooditude.databinding.FragmentDeliverResultsBinding
import com.health.mental.mooditude.databinding.FragmentGuidanceBinding
import com.health.mental.mooditude.databinding.FragmentRegulateEmotionsBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [GuidanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuidanceFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentGuidanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGuidanceBinding.inflate(inflater, container, false)
        val view = binding.root

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvTitle.setText(Html.fromHtml(getString(R.string.personalized_guidance), 0))
        } else {
            binding.tvTitle.setText(Html.fromHtml(getString(R.string.personalized_guidance)))
        }

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvDesc.setText(Html.fromHtml(getString(R.string.guidance_desc), 0))
        } else {
            binding.tvDesc.setText(Html.fromHtml(getString(R.string.guidance_desc)))
        }

        binding.btnContinue.setOnClickListener {
            (requireActivity() as OnBoardingActivity).onJoinMooditudeBtnClicked()
        }

        return view
    }
}