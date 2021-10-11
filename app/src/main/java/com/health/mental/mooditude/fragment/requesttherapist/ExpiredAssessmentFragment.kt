package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.databinding.FragmentExpiredAssessmentBinding
import com.health.mental.mooditude.fragment.BaseFragment

/**
 * A simple [Fragment] subclass.
 * Use the [ExpiredAssessmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExpiredAssessmentFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentExpiredAssessmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentExpiredAssessmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnTakeAssessment.setOnClickListener {
            (requireActivity() as BaseActivity).startM3Assessment(false)
            Handler(Looper.getMainLooper()).postDelayed(object :Runnable {
                override fun run() {
                    if(activity != null && isAdded) {
                        requireActivity().onBackPressed()
                    }
                }

            }, 500)
        }

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvTitle.setText(Html.fromHtml(getString(R.string.your_wellbeing_score), 0))
        } else {
            binding.tvTitle.setText(Html.fromHtml(getString(R.string.your_wellbeing_score)))
        }

        return root
    }


}