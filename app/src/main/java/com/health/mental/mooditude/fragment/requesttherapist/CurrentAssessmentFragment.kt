package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.activity.FindMyTherapistActivity
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.databinding.FragmentCurrentAssessmentBinding
import com.health.mental.mooditude.fragment.BaseFragment

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentAssessmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentAssessmentFragment(var m3Assessment: M3Assessment) : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentCurrentAssessmentBinding

    companion object {

        fun newInstance(param1: M3Assessment): CurrentAssessmentFragment {
            return CurrentAssessmentFragment(param1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCurrentAssessmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (requireActivity() as BaseActivity).setupAssessmentTopbar(
            m3Assessment,
            binding.assessmentTopbar
        )

        binding.assessmentTopbar.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.home_background))
        binding.btnUseThis.setOnClickListener {
            (requireActivity() as FindMyTherapistActivity).onUseThisAssessmentBtnClicked()
        }
        binding.btnRetake.setOnClickListener {
            (requireActivity() as BaseActivity).startM3Assessment(false)
            //requireActivity().onBackPressed()
        }

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvTitle.setText(Html.fromHtml(getString(R.string.your_wellbeing_score), 0))
        } else {
            binding.tvTitle.setText(Html.fromHtml(getString(R.string.your_wellbeing_score)))
        }

        return root
    }

    fun updateUi(assessment: M3Assessment) {
        m3Assessment = assessment
        (requireActivity() as BaseActivity).setupAssessmentTopbar(
            m3Assessment,
            binding.assessmentTopbar
        )
    }


}