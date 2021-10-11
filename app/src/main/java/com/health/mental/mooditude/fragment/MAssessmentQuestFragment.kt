package com.health.mental.mooditude.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.MAssessmentFlowDialog
import com.health.mental.mooditude.data.model.M3Question
import com.health.mental.mooditude.databinding.FragmentAssessmentQuestBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.model.ProfileOption
import com.health.mental.mooditude.utils.PARAM_QUESTION


/**
 * A simple [Fragment] subclass.
 * Use the [MAssessmentQuestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MAssessmentQuestFragment : BaseFragment(), Animation.AnimationListener {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentAssessmentQuestBinding

    private var mQuestion: M3Question? = null
    private var mListOptions = ArrayList<ProfileOption>()
    private var mSelectedTextView: TextView? = null
    private var mSelectedViewSeperator: View? = null
    private var mQuestionAppearTime:Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mQuestion = arguments?.getSerializable(PARAM_QUESTION) as M3Question?
    }

    companion object {

        fun newInstance(param1: M3Question): MAssessmentQuestFragment? {
            val fragment = MAssessmentQuestFragment()
            val args = Bundle()
            args.putSerializable(PARAM_QUESTION, param1)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAssessmentQuestBinding.inflate(inflater, container, false)
        val view = binding.root

        //First reset error text
        binding.tvTitle.text = mQuestion!!.text

        //listview
        //val recyclerLayoutManager = LinearLayoutManager(requireActivity())
        //binding.listEntries.setLayoutManager(recyclerLayoutManager)


        binding.tvMostTime.setOnClickListener {
            onAnswerSelected(4, it as TextView, binding.viewSeperator1)
        }
        binding.tvOften.setOnClickListener {
            onAnswerSelected(3, it as TextView, binding.viewSeperator2)
        }
        binding.tvSometime.setOnClickListener {
            onAnswerSelected(2, it as TextView, binding.viewSeperator3)
        }
        binding.tvRarely.setOnClickListener {
            onAnswerSelected(1, it as TextView, binding.viewSeperator4)
        }
        binding.tvNotAtAll.setOnClickListener {
            onAnswerSelected(0, it as TextView, binding.viewSeperator5)
        }

        if (mQuestion!!.selectedOption != null) {
            //holder.tvDesc.visibility = View.VISIBLE
            when (mQuestion!!.selectedOption) {
                0 -> {
                    selectOption(binding.tvNotAtAll, binding.viewSeperator5)
                }
                1 -> {
                    selectOption(binding.tvRarely, binding.viewSeperator4)
                }
                2 -> {
                    selectOption(binding.tvSometime, binding.viewSeperator3)
                }
                3 -> {
                    selectOption(binding.tvOften, binding.viewSeperator2)
                }
                4 -> {
                    selectOption(binding.tvMostTime, binding.viewSeperator1)
                }
            }
        }

        //call it to update gradient
        //(requireActivity() as MAssessmentFlowDialog).answerSelected(mQuestion!!.selectedOption)
        //save the current time, if user selects any option then just update field timeToAnswer
        mQuestionAppearTime = System.currentTimeMillis()
        debugLog(TAG, "Question time to answer time is : " + mQuestion!!.timeToAnswerInSeconds)
        return view
    }

    private fun selectOption(text: TextView, viewSeperator:View) {
        viewSeperator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        text.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen._14sdp).toFloat()
        )
        mSelectedTextView = text
        mSelectedViewSeperator = viewSeperator
    }

    private fun deSelectOption(text: TextView, viewSeperator: View) {
        viewSeperator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.boarding_text))
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.boarding_text))
        text.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen._12sdp).toFloat()
        )
    }

    private fun onAnswerSelected(position: Int, text: TextView, viewSeperator: View) {
        debugLog(TAG, "option selected : " + position)
        if (position >= 0) {
            //setNextEnabled(true)
            mQuestion!!.selectedOption = mQuestion!!.options.get(position).id
            debugLog(TAG,
                "Selected answer is : " + mQuestion!!.options.get(position).id + ": " + mQuestion!!.options.get(
                    position
                ).text
            )
            //save time to answer
            val milliSeconds = System.currentTimeMillis() - mQuestionAppearTime
            mQuestion!!.timeToAnswerInSeconds = (milliSeconds/1000).toInt()
            debugLog(TAG,"Question time to answer time : " + mQuestion!!.timeToAnswerInSeconds)

        } else {
            //setNextEnabled(false)
            mQuestion!!.selectedOption = -1
        }
        (requireActivity() as MAssessmentFlowDialog).answerSelected(mQuestion!!.selectedOption)

        if(mSelectedTextView != null && mSelectedViewSeperator != null) {
            deSelectOption(mSelectedTextView!!, mSelectedViewSeperator!!)
        }
        selectOption(text, viewSeperator)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var anim =  super.onCreateAnimation(transit, enter, nextAnim)
        if (anim == null && nextAnim != 0) {
            anim = android.view.animation.AnimationUtils.loadAnimation(requireActivity(), nextAnim)
        }
        anim!!.setAnimationListener(this)
        return anim
    }

    override fun onAnimationStart(animation: Animation?) {
        //(requireActivity() as MAssessmentFlowDialog).setBackgroundGradient()
    }

    override fun onAnimationEnd(animation: Animation?) {
        (requireActivity() as MAssessmentFlowDialog).setBackgroundGradient()
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }


}
