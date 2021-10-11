package com.health.mental.mooditude.fragment.journal

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.HowYouFeelActivity
import com.health.mental.mooditude.adapter.UserActivityAdapter
import com.health.mental.mooditude.custom.CustomGridView
import com.health.mental.mooditude.custom.CustomTypefaceSpan
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.entity.UserActivity
import com.health.mental.mooditude.data.model.ActivityGroup
import com.health.mental.mooditude.databinding.FragmentSituationBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.UiUtils


/**
 * A simple [Fragment] subclass.
 * Use the [SituationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SituationFragment() : BaseFragment(), UserActivityAdapter.OnActivitySelectionListener {

    companion object {
        fun newInstance(activities: ArrayList<UserActivity>): SituationFragment{
            val args = Bundle()
            if(activities.size > 0) {
                args.putString("list", Gson().toJson(activities))
            }
            val fragment = SituationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentSituationBinding
    private var listAdapters = ArrayList<UserActivityAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSituationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //check if user has cancelled earlier
        val shouldShowSituationInfo = SharedPreferenceManager.getSituationInfoFlag()
        if(shouldShowSituationInfo == null || !shouldShowSituationInfo) {
            val titleText = requireContext().getText(R.string.describe_situation) as SpannedString
            // get all the annotation spans from the text
            val annotations =
                titleText.getSpans(0, titleText.length, android.text.Annotation::class.java)

            // create a copy of the title text as a SpannableString.
            val spannableString = SpannableString(titleText)

            // iterate through all the annotation spans
            for (annotation in annotations) {
                // look for the span with the key font
                if (annotation.key == "font") {
                    val fontName = annotation.value
                    // check the value associated to the annotation key
                    if (fontName == "fa_pro_solid_900") {
                        // create the typeface
                        val typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.fa_pro_solid_900)
                        // set the span at the same indices as the annotation
                        spannableString.setSpan(
                            CustomTypefaceSpan(typeface!!),
                            titleText.getSpanStart(annotation),
                            titleText.getSpanEnd(annotation),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }

            binding.tvInfo.setText(spannableString)
        }
        else {
            binding.infoLayout.visibility = View.GONE
        }

        binding.ivCancel.setOnClickListener {
            binding.infoLayout.visibility = View.GONE
            SharedPreferenceManager.setSituationInfoFlag(true)
        }
        createUserActivityViews()

        return root
    }

    fun createUserActivityViews() {
        val activity = requireActivity()
        val context = requireContext()


        //Remove all views
        binding.activityGroupContainer.removeAllViews()
        listAdapters.clear()
        if (activity is HowYouFeelActivity) {
            //First let's create hashmap
            val map = LinkedHashMap<String, ArrayList<UserActivity>>()

            //Recent list
            val recentList = (activity as HowYouFeelActivity).getUserActivityRecentlyUsedList()
            if (recentList != null && recentList.size > 0) {
                map.put(
                    context.getString(R.string.activity_group_recent),
                    recentList as ArrayList<UserActivity>
                )
            }

            val list = (activity as HowYouFeelActivity).getUserActivityList()
            if (list != null) {

                for (item in list) {
                    val group = item.group.getLocalizedName(context)
                    var groupList = map.get(group)
                    if (groupList == null) {
                        groupList = ArrayList()
                        map.put(group, groupList)
                    }
                    groupList.add(item)
                }

                //Now add views
                val containerRoot = binding.activityGroupContainer

                //First add Recent
                val recentKey = context.getString(R.string.activity_group_recent)
                if (map.containsKey(recentKey)) {
                    addViews(containerRoot, recentKey, map.get(recentKey))
                }

                //then add custom
                val customKey = ActivityGroup.other.getLocalizedName(context)
                if (map.containsKey(customKey)) {
                    addViews(containerRoot, customKey, map.get(customKey))
                }
                for (key in map.keys) {
                    if (key.equals(recentKey)) continue
                    if (key.equals(customKey)) continue

                    val activityList = map.get(key)
                    addViews(containerRoot, key, activityList)
                }
            }
        }

        makeSelections()
        enableDisableNext()
    }

    private fun addViews(
        containerRoot: ViewGroup,
        key: String,
        activityList: ArrayList<UserActivity>?
    ) {
        if (activityList != null && activityList.size > 0) {

            val layout = layoutInflater.inflate(
                R.layout.view_user_activity,
                containerRoot,
                false
            )
            val tvTitle = layout.findViewById<TextView>(R.id.tv_group_title)
            tvTitle.setText(key)

            val gridView = layout.findViewById<CustomGridView>(R.id.gridview)
            //gridView.isNestedScrollingEnabled = false
            val adapter = UserActivityAdapter(requireContext(), activityList, this)
            listAdapters.add(adapter)
            gridView.adapter = adapter
            binding.activityGroupContainer.addView(layout)
        }
    }


    fun updateSelections(mUserEntryInfo: Entry): Boolean {

        val listSelectedActivities = ArrayList<UserActivity>()
        for (adapter in listAdapters) {
            listSelectedActivities.addAll(adapter.getSelectedActivities())
        }

        if (listSelectedActivities.size == 0) {
            UiUtils.showErrorToast(requireActivity(), getString(R.string.please_select_one))
            return false
        }

        mUserEntryInfo.activities.clear()
        for (userActivity in listSelectedActivities) {
            mUserEntryInfo.activities.add(userActivity)
        }

        debugLog(TAG, "Total selection : " + mUserEntryInfo.activities.toString())

        return true
    }

    override fun onActivityTapped() {
        enableDisableNext()
    }

    fun enableDisableNext() {
        val listSelectedActivities = ArrayList<UserActivity>()
        for (adapter in listAdapters) {
            listSelectedActivities.addAll(adapter.getSelectedActivities())
        }

        if(activity != null && isAdded) {
            if (listSelectedActivities.size == 0) {
                (requireActivity() as HowYouFeelActivity).setNextEnabled(false)
            }
            else {
                (requireActivity() as HowYouFeelActivity).setNextEnabled(true)
            }
        }
    }

    private fun makeSelections() {
        if(arguments != null && requireArguments().containsKey("list")) {
            val listStr = requireArguments().getString("list")
            val list = Gson().fromJson(listStr, Array<UserActivity>::class.java).toList()

            if(list != null && list.size > 0) {
                val selectedList = ArrayList<UserActivity>(list)
                for(adapter in listAdapters) {
                    val listToRemove = adapter.setSelectedActivities(selectedList)
                    selectedList.removeAll(listToRemove)
                }
            }
        }
    }
}