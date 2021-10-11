package com.health.mental.mooditude.fragment.requesttherapist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.activity.TherapistFeedbackActivity
import com.health.mental.mooditude.data.model.TherapistFeedback
import com.health.mental.mooditude.databinding.FragmentTherapistFeedbackPositiveBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [TherapistRequestPositiveFeedbackFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TherapistRequestPositiveFeedbackFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentTherapistFeedbackPositiveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTherapistFeedbackPositiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnSubmit.setOnClickListener {
            val feedback = TherapistFeedback()
            feedback.comments = binding.etMessage.text.toString()
            feedback.workingWithTherapist = binding.switchWorking.isChecked
            //feedback.noOneCalled = false
            feedback.rating = binding.ratingBar.numStars
            (requireActivity() as TherapistFeedbackActivity).onSubmitFeedbackBtnClicked(feedback)
        }
        return root
    }


}