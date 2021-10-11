package com.health.mental.mooditude.fragment.journal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.HowYouFeelActivity
import com.health.mental.mooditude.activity.JournalPromptDetailsActivity
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.model.journal.EmotionType
import com.health.mental.mooditude.databinding.FragmentMoodBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.DATE_FORMAT_MOOD_TIME
import com.health.mental.mooditude.utils.UiUtils
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [MoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoodFragment() : BaseFragment() {

    companion object {
        fun newInstance(emotionType:EmotionType, intensity:Int, date: Date? = null): MoodFragment{
            val args = Bundle()
            args.putString("type", emotionType.toString())
            args.putInt("intensity", intensity)
            if(date != null) {
                args.putLong("date", date.time)
            }
            val fragment = MoodFragment()
            fragment.arguments = args
            return fragment
        }
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentMoodBinding
    private var emotionType: EmotionType? = null
    private var emotionIntensity : Int = 0

    private var mSelectedView:View? = null
    private var mSelectedBadgeView:TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvDate.tag = System.currentTimeMillis()
        binding.tvDate.text = SimpleDateFormat(DATE_FORMAT_MOOD_TIME, Locale.US).format(System.currentTimeMillis())
        binding.tvDate.setOnClickListener {
            if(requireActivity() is JournalPromptDetailsActivity) {
                //do not allow to change date
            }
            else {
                showDatePickerDialog(it.tag as Long)
            }
        }

        binding.tvElevated.visibility = View.GONE
        binding.tvHigh.visibility = View.GONE
        binding.tvNormal.visibility = View.GONE
        binding.tvLow.visibility = View.GONE
        binding.tvWorst.visibility = View.GONE

        addOnClickListener(EmotionType.worst, binding.ivMoodWorst, binding.tvWorst, binding.ivMoodHigh, binding.ivMoodLow, binding.ivMoodNormal, binding.ivMoodElevated)
        addOnClickListener(EmotionType.low, binding.ivMoodLow, binding.tvLow, binding.ivMoodHigh, binding.ivMoodWorst, binding.ivMoodNormal, binding.ivMoodElevated)
        addOnClickListener(EmotionType.normal, binding.ivMoodNormal, binding.tvNormal, binding.ivMoodHigh, binding.ivMoodWorst, binding.ivMoodLow, binding.ivMoodElevated)
        addOnClickListener(EmotionType.high, binding.ivMoodHigh, binding.tvHigh, binding.ivMoodNormal, binding.ivMoodWorst, binding.ivMoodLow, binding.ivMoodElevated)
        addOnClickListener(EmotionType.elevated, binding.ivMoodElevated, binding.tvElevated, binding.ivMoodHigh, binding.ivMoodWorst, binding.ivMoodLow, binding.ivMoodNormal)

        //check for extras
        if(arguments != null) {
            val type = requireArguments().getString("type")
            val intensity = requireArguments().getInt("intensity")
            if(type != null && intensity > 0) {
                val selectedType = EmotionType.valueOf(type)
                makeSelection(selectedType, intensity)
            }
            val date = requireArguments().getLong("date")
            if(date != 0L) {
                binding.tvDate.tag = date
                binding.tvDate.text = SimpleDateFormat(DATE_FORMAT_MOOD_TIME, Locale.US).format(date)
            }
        }

        return root
    }

    private fun makeSelection(selectedType: EmotionType, intensity1: Int) {
        //make a selection programmatically
        var intensity = intensity1
        while(intensity > 0) {
            when (selectedType) {
                EmotionType.worst -> binding.ivMoodWorst.callOnClick()
                EmotionType.low -> binding.ivMoodLow.callOnClick()
                EmotionType.normal -> binding.ivMoodNormal.callOnClick()
                EmotionType.high -> binding.ivMoodHigh.callOnClick()
                EmotionType.elevated -> binding.ivMoodElevated.callOnClick()
            }
            intensity--
        }
    }


    private fun addOnClickListener(type: EmotionType,
                                   viewTarget:View, badgeView:TextView, otherView1:View, otherView2: View, otherView3:View, otherView4: View) {
        viewTarget.setOnClickListener {
            if(it.tag == null) {
                it.tag = 1
            }
            it.alpha = 1f
            emotionType = type
            emotionIntensity = it.tag as Int
            val tag = (it.tag as Int)
            //show badge
            badgeView.visibility = View.VISIBLE
            badgeView.text = tag.toString()
            it.layoutParams.width = resources.getDimensionPixelSize(R.dimen._40sdp)
            it.layoutParams.height = resources.getDimensionPixelSize(R.dimen._40sdp)


            val alphaVal = 1 - (tag.toFloat()/5)
            otherView1.alpha = alphaVal
            otherView2.alpha = alphaVal
            otherView3.alpha = alphaVal
            otherView4.alpha = alphaVal

            it.tag = tag+1
            if(tag >= 5) {
                it.isEnabled = false

                otherView1.isEnabled = false
                otherView2.isEnabled = false
                otherView3.isEnabled = false
                otherView4.isEnabled = false
            }

            //check for selected view
            if(mSelectedView != null && mSelectedView != it) {
                mSelectedView!!.tag = null
                mSelectedView!!.layoutParams.width = resources.getDimensionPixelSize(R.dimen._35sdp)
                mSelectedView!!.layoutParams.height = resources.getDimensionPixelSize(R.dimen._35sdp)

                if(mSelectedBadgeView != null) {
                    mSelectedBadgeView!!.visibility = View.GONE
                }
                mSelectedView!!.isSelected = false
            }
            mSelectedView = it
            mSelectedBadgeView = badgeView
            mSelectedView!!.isSelected = true

            enableDisableNext()
        }

    }

    // listener which is triggered when the
    // time is picked from the time picker dialog
    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = binding.tvDate.tag as Long
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                binding.tvDate.text = SimpleDateFormat(DATE_FORMAT_MOOD_TIME, Locale.US).format(calendar.timeInMillis)
                binding.tvDate.tag = calendar.timeInMillis
            }
        }

    // listener which is triggered when the
    // date is picked from the date picker dialog
    private val datePickerDialogListener: DatePickerDialog.OnDateSetListener =
        object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = binding.tvDate.tag as Long
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.tvDate.text = SimpleDateFormat(DATE_FORMAT_MOOD_TIME, Locale.US).format(calendar.timeInMillis)
                binding.tvDate.tag = calendar.timeInMillis

                showTimePickerDialog(calendar.timeInMillis)
            }
        }


    private fun showDatePickerDialog(time:Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayofmonth = calendar.get(Calendar.DAY_OF_MONTH)


        val datePicker: DatePickerDialog = DatePickerDialog(
            // pass the Context
            requireContext(),
            // listener to perform task
            // when time is picked
            datePickerDialogListener,
            // default hour when the time picker
            // dialog is opened
            year,
            // default minute when the time picker
            // dialog is opened
            month,
            // 24 hours time picker is
            // false (varies according to the region)
            dayofmonth
        )

        //Do not allow future dates
        datePicker.datePicker.maxDate = System.currentTimeMillis()

        // then after building the timepicker
        // dialog show the dialog to user
        datePicker.show()
    }


    private fun showTimePickerDialog(time:Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)


        val timePicker: TimePickerDialog = TimePickerDialog(
            // pass the Context
            requireContext(),
            // listener to perform task
            // when time is picked
            timePickerDialogListener,
            // default hour when the time picker
            // dialog is opened
            hour,
            // default minute when the time picker
            // dialog is opened
            minute,
            // 24 hours time picker is
            // false (varies according to the region)
            false
        )

        // then after building the timepicker
        // dialog show the dialog to user
        timePicker.show()
    }

    fun updateMoodValues(mUserEntryInfo: Entry): Boolean {

        debugLog(TAG, "EmotionType : " + emotionType + " : " + emotionIntensity)
        if(emotionType == null || emotionIntensity == 0) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.please_select_mood))
            return false
        }
        mUserEntryInfo.emotion = emotionType
        mUserEntryInfo.emotionIntensity = emotionIntensity

        //Datetime
        mUserEntryInfo.postedDate = Date(binding.tvDate.tag as Long)
        return true
    }

    fun enableDisableNext() {
        //enable next button
        if(activity != null && isAdded) {
            if (requireActivity() is HowYouFeelActivity) {
                if (emotionType == null || emotionIntensity == 0) {
                    (requireActivity() as HowYouFeelActivity).setNextEnabled(false)
                } else {
                    (requireActivity() as HowYouFeelActivity).setNextEnabled(true)
                }
            } else if (requireActivity() is JournalPromptDetailsActivity) {
                if (emotionType == null || emotionIntensity == 0) {
                    (requireActivity() as JournalPromptDetailsActivity).setNextEnabled(false)
                } else {
                    (requireActivity() as JournalPromptDetailsActivity).setNextEnabled(true)
                }
            }
        }
    }
}