package com.health.mental.mooditude.fragment.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.data.model.journal.JournalPromptStep
import com.health.mental.mooditude.databinding.FragmentJournalPromptStepBinding
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [JournalStepFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JournalStepFragment(val promptStep: JournalPromptStep) : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentJournalPromptStepBinding

    companion object {
        fun newInstance(promptStep: JournalPromptStep): JournalStepFragment {
            val fragment = JournalStepFragment(promptStep)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentJournalPromptStepBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvText1.text = promptStep.title
        return root
    }


}