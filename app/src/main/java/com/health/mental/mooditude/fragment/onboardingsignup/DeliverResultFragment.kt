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
import com.health.mental.mooditude.databinding.FragmentDeliverResultsBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [DeliverResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeliverResultFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentDeliverResultsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDeliverResultsBinding.inflate(inflater, container, false)
        val view = binding.root

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvDesc.setText(Html.fromHtml(getString(R.string.designed_by), 0))
        } else {
            binding.tvDesc.setText(Html.fromHtml(getString(R.string.designed_by)))
        }

        binding.btnContinue.setOnClickListener {
            (requireActivity() as OnBoardingActivity).onShowMeBtnClicked()
        }

        return view
    }
}