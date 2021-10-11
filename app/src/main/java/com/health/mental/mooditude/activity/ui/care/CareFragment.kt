package com.health.mental.mooditude.activity.ui.care

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.FragmentCareBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.fragment.requesttherapist.HealthTherapistsFragment
import com.health.mental.mooditude.fragment.requesttherapist.PaymentMethodFragment
import com.health.mental.mooditude.utils.getUserCountry
import com.health.mental.mooditude.utils.isCountrySupported

class CareFragment : BaseFragment() {


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentCareBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCareBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if(isCountrySupported(requireContext())) {
            //add fragment
            val fragment = MainFragment()
            addFragment(R.id.layout_container, fragment, true)
        }
        else {
            val textView = inflater.inflate(R.layout.view_not_enough_data, root as ViewGroup, false)
            (textView as TextView).setText(R.string.comming_soon)
            textView.visibility = View.VISIBLE
            textView.setPadding(0,resources.getDimensionPixelSize(R.dimen._200sdp),0,0)
            binding.layoutContainer.addView(textView)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onRequestTherapistBtnClicked() {
            addFragment(
                R.id.layout_container,
                childFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName),
                HealthTherapistsFragment(),
                true
            )
    }

    fun onContinueBtnClicked() {

        //Let's first check if we have any assessment
        addFragment(
            R.id.layout_container,
            childFragmentManager.findFragmentByTag(HasAssessmentFragment::class.java.simpleName),
            StateFragment(),
            true
        )
    }

    fun onStateSelected(state: String) {
        if(state.contains("ind", true)) {
            //Let's first check if we have any assessment
            addFragment(
                R.id.layout_container,
                childFragmentManager.findFragmentByTag(StateFragment::class.java.simpleName),
                NoStateOperatingFragment(state),
                true
            )
        }
        else { //ask for payment
            addFragment(
                R.id.layout_container,
                childFragmentManager.findFragmentByTag(StateFragment::class.java.simpleName),
                PaymentMethodFragment(),
                true
            )

        }

    }

    fun onPaymentSelected(payment: String) {
        if(payment.contains("ind", true)) {
            //Let's first check if we have any assessment
            addFragment(
                R.id.layout_container,
                childFragmentManager.findFragmentByTag(StateFragment::class.java.simpleName),
                NoStateOperatingFragment(payment),
                true
            )
        }

    }

}