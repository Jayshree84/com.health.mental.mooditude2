package com.health.mental.mooditude.activity.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.databinding.FragmentTrackingBinding
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.viewedPastAssessments
import com.health.mental.mooditude.services.instrumentation.viewedPastAssment

class TrackingFragment : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentTrackingBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var mode = 0
        //now check for arguments
        if(arguments != null) {
            val arguments = requireArguments()
            mode = arguments.getInt("mode")
        }

        //add fragment
        val fragment = MainFragment.newInstance(mode)
        addFragment(R.id.layout_container, fragment, true)

        return root
    }


    fun onViewFullReportBtnClicked(m3Assessment: M3Assessment) {
        //now add fragment for fullreport
        addFragment(
            R.id.layout_container,
            childFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName),
            FullReportFragment.newInstance(m3Assessment, 0),
            true
        )
        //(requireActivity() as HomeActivity).showCloseBtn()
        //(requireActivity() as BaseActivity).setPageTitle(requireActivity().findViewById(R.id.main_toolbar), getString(R.string.mental_wellbeing_score))
    }

    fun onViewFullReportFromListClicked(m3Assessment: M3Assessment) {
        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            childFragmentManager.findFragmentByTag(AssessmentListFragment::class.java.simpleName),
            FullReportFragment.newInstance(m3Assessment, 0),
            true
        )
        //(requireActivity() as HomeActivity).showCloseBtn()
        //(requireActivity() as BaseActivity).setPageTitle(requireActivity().findViewById(R.id.main_toolbar), getString(R.string.mental_wellbeing_score))

        //log event
        EventCatalog.instance.viewedPastAssment(m3Assessment)
    }

    fun onShowListBtnClicked() {
        addFragment(
            R.id.layout_container,
            childFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName),
            AssessmentListFragment(),
            true
        )

        //log event
        EventCatalog.instance.viewedPastAssessments()
    }

    fun onViewScoreBtnClicked(m3Assessment: M3Assessment) {
        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            childFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName),
            FullReportFragment.newInstance(m3Assessment, 1),
            true
        )
        //(requireActivity() as HomeActivity).showCloseBtn()
        //(requireActivity() as BaseActivity).setPageTitle(requireActivity().findViewById(R.id.main_toolbar), getString(R.string.mental_wellbeing_score))
    }


}