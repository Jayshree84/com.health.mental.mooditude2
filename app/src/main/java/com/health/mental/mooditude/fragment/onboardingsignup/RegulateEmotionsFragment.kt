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
import com.health.mental.mooditude.databinding.FragmentRegulateEmotionsBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [RegulateEmotionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegulateEmotionsFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentRegulateEmotionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegulateEmotionsBinding.inflate(inflater, container, false)
        val view = binding.root

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvTitle.setText(Html.fromHtml(getString(R.string.regulate_emotions), 0))
        } else {
            binding.tvTitle.setText(Html.fromHtml(getString(R.string.regulate_emotions)))
        }

        binding.btnContinue.setOnClickListener {
            (requireActivity() as OnBoardingActivity).onGuidedEntryBtnClicked()
        }

        binding.btnSkip.setOnClickListener {
            (requireActivity() as OnBoardingActivity).onSkipFromRegulateEmotions()
        }

        return view
    }
}