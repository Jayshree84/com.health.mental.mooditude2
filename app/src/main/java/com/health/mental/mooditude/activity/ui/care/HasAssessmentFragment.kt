package com.health.mental.mooditude.activity.ui.care

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.databinding.FragmentCareHasAssessmentBinding
import com.health.mental.mooditude.fragment.BaseFragment

/**
 * A simple [Fragment] subclass.
 * Use the [HasAssessmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HasAssessmentFragment(val mM3Assessment: M3Assessment) : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentCareHasAssessmentBinding

    companion object {

        fun newInstance(param1: M3Assessment): HasAssessmentFragment {
            val fragment = HasAssessmentFragment(param1)
            /*val bundle = Bundle()
            bundle.putInt("tab_position", tabPosition)
            fragment.arguments = bundle*/
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCareHasAssessmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (requireActivity() as BaseActivity).setupAssessmentTopbar(mM3Assessment, binding.assessmentTopbar)
        binding.btnContinue.setOnClickListener {
            (parentFragment as CareFragment).onContinueBtnClicked()
        }

        return root
    }



}