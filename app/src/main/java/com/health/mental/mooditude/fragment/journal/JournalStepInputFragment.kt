package com.health.mental.mooditude.fragment.journal

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.HowYouFeelActivity
import com.health.mental.mooditude.activity.JournalPromptDetailsActivity
import com.health.mental.mooditude.data.model.journal.JournalPromptStep
import com.health.mental.mooditude.databinding.FragmentJournalPromptInputBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [JournalStepInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JournalStepInputFragment(val promptStep: JournalPromptStep) : BaseFragment(), TextWatcher {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentJournalPromptInputBinding

    companion object {
        fun newInstance(promptStep: JournalPromptStep): JournalStepInputFragment {
            val fragment = JournalStepInputFragment(promptStep)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentJournalPromptInputBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvText1.text = promptStep.title
        binding.etMessage.hint = promptStep.inputPlaceholder
        debugLog(TAG, "Hint : " + promptStep.inputPlaceholder)

        binding.etMessage.addTextChangedListener(this)

        if(promptStep.userInput != null) {
            binding.etMessage.setText(promptStep.userInput)
        }
        return root
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {

        if (s == null || s.length == 0) {
            binding.tvTotalWords.setText("")

        } else {
            // separate string around spaces
            val wordsAry =  s.split("\\s+".toRegex()).toTypedArray()
            var totalWords = 0
            for(word in wordsAry) {
                if(word.trim().isNotEmpty()) totalWords++
            }

            if(totalWords == 1) {
                binding.tvTotalWords.setText(String.format(getString(R.string.word), totalWords))
            }
            else {
                binding.tvTotalWords.setText(String.format(getString(R.string.words), totalWords))
            }
        }
        enableDisableNext()
    }

    fun getUserInput() = binding.etMessage.text.toString()
    fun enableDisableNext() {
        //enable next button
        if(activity != null && isAdded) {
            val text = binding.etMessage.text.toString()
            if (text.trim().isEmpty()) {
                (requireActivity() as JournalPromptDetailsActivity).setNextEnabled(false)
            }
            else {
                (requireActivity() as JournalPromptDetailsActivity).setNextEnabled(true)
            }
        }
    }
}