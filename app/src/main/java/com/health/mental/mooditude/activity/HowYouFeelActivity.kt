package com.health.mental.mooditude.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.entity.UserActivity
import com.health.mental.mooditude.data.model.journal.EmotionType
import com.health.mental.mooditude.databinding.ActivityHowYouFeelBinding
import com.health.mental.mooditude.fragment.journal.AddSituationFragment
import com.health.mental.mooditude.fragment.journal.MoodFragment
import com.health.mental.mooditude.fragment.journal.SituationFragment
import com.health.mental.mooditude.utils.UiUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HowYouFeelActivity : BaseActivity() {

    private lateinit var binding: ActivityHowYouFeelBinding
    private var mUserActivityList: List<UserActivity>? = null
    private var mUserActivityFrequentList: List<UserActivity>? = null
    private var mUserEntryInfo = Entry()
    private var mIsInEditMode:Boolean = false

    enum class ScreenMode {
        Mood,
        Situation,
        AddSituation
    }

    private var mScreenMode = ScreenMode.Mood
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHowYouFeelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    override fun initComponents() {
        val allUserActivities = DBManager.instance.fetchAllUserActivityRecords()

        //Observer on data
        allUserActivities.observe(this, Observer {
            CoroutineScope(Dispatchers.IO).launch {
                mUserActivityList = it
                mUserActivityFrequentList = DBManager.instance.fetchRecentUserActivityRecords()

                //Code to update the list
                runOnUiThread {
                    val fragment =
                        supportFragmentManager.findFragmentByTag(SituationFragment::class.java.simpleName)
                    if (fragment != null) {
                        (fragment as SituationFragment).createUserActivityViews()
                    }
                }
            }

        })
        //set title
        binding.toolbar.tvTitle.text = ""

        //enable/disable - back n next
        binding.toolbar.btnClose.setOnClickListener {
            finish()
        }
        binding.toolbar.btnForward.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.btnPlus.setOnClickListener {
            if (mScreenMode == ScreenMode.Situation) {
                mScreenMode = ScreenMode.AddSituation
                updateUI()
            }
        }

        binding.toolbar.btnNext2.setOnClickListener {
            when (mScreenMode) {
                ScreenMode.Mood -> {
                    val fragment =
                        supportFragmentManager.findFragmentByTag(MoodFragment::class.java.simpleName)
                    if (fragment != null) {
                        val success =
                            (fragment as MoodFragment).updateMoodValues(mUserEntryInfo)

                        if (success) {
                            onMoodSelected()
                        }
                    }
                }
                ScreenMode.Situation -> {
                    val fragment =
                        supportFragmentManager.findFragmentByTag(SituationFragment::class.java.simpleName)
                    if (fragment != null) {
                        val success =
                            (fragment as SituationFragment).updateSelections(mUserEntryInfo)

                        if (success) {
                            onSituationSelected()
                        }
                    }
                }
                ScreenMode.AddSituation -> {
                    val fragment =
                        supportFragmentManager.findFragmentByTag(AddSituationFragment::class.java.simpleName)
                    if (fragment != null) {
                        val record =
                            (fragment as AddSituationFragment).createRecord()

                        if (record != null) {
                            //Save record
                            DBManager.instance.saveUserActivityRecord(record)
                            onNewActivityAdded()
                        }
                    }
                }

            }
        }

        //First fragment
        mScreenMode = ScreenMode.Mood
        updateUI()
    }

    /**
     * Enables/disables NEXT button
     */
    fun setNextEnabled(flag: Boolean) {
        binding.toolbar.btnNext2.isEnabled = flag
    }

    fun getUserActivityList(): List<UserActivity>? {
        return mUserActivityList
    }

    private fun updateUI(addFragments: Boolean = true) {
        binding.toolbar.tvTitle.setText("")
        when (mScreenMode) {
            ScreenMode.Mood -> {
                //Add first fragment
                if (addFragments) {

                    //check for edit mode
                    if (intent.extras != null && intent.extras!!.containsKey("edit_mode")) {
                        mIsInEditMode = intent.extras!!.getBoolean("edit_mode", false)
                        val entry = intent.extras!!.getString("entry")
                        if (entry != null) {
                            this.mUserEntryInfo = Gson().fromJson(entry, Entry::class.java)
                            val fragment = MoodFragment.newInstance(
                                this.mUserEntryInfo.emotion!!, this.mUserEntryInfo.emotionIntensity,
                                this.mUserEntryInfo.postedDate
                            )
                            addFragment(R.id.layout_container, fragment, true)
                        }
                    } else {
                        var type = EmotionType.elevated
                        var intensity = 0

                        if (intent.extras != null) {

                            if (intent.extras!!.containsKey("type") && intent.extras!!.containsKey("intensity")) {
                                type = EmotionType.valueOf(intent.extras!!.getString("type")!!)
                                intensity = intent.extras!!.getInt("intensity")
                            }
                        }
                        val fragment = MoodFragment.newInstance(type, intensity)
                        addFragment(R.id.layout_container, fragment, true)
                    }
                }

                //update toolbar controls
                binding.toolbar.btnForward.visibility = View.GONE
                binding.toolbar.btnNext2.visibility = View.VISIBLE
                binding.toolbar.btnNext2.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_btn_next
                    )
                )
                binding.toolbar.btnPlus.visibility = View.GONE
                binding.toolbar.btnClose.visibility = View.VISIBLE
                setNextEnabled(false)

                //check for selection on backpress
                val fragment =
                    supportFragmentManager.findFragmentByTag(MoodFragment::class.java.simpleName)
                if (fragment != null) {
                    (fragment as MoodFragment).enableDisableNext()
                }
            }
            ScreenMode.Situation -> {
                //Add first fragment
                if (addFragments) {

                    val fragment = SituationFragment.newInstance(mUserEntryInfo.activities)
                    //check for edit mode

                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(MoodFragment::class.java.simpleName),
                        fragment,
                        true
                    )
                }

                //update toolbar controls
                binding.toolbar.btnForward.visibility = View.VISIBLE
                binding.toolbar.btnNext2.visibility = View.VISIBLE
                binding.toolbar.btnNext2.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_btn_checkoption
                    )
                )
                binding.toolbar.btnClose.visibility = View.VISIBLE
                binding.toolbar.btnPlus.visibility = View.VISIBLE
                setNextEnabled(false)

                //check for selection on backpress
                val fragment =
                    supportFragmentManager.findFragmentByTag(SituationFragment::class.java.simpleName)
                if (fragment != null) {
                    (fragment as SituationFragment).enableDisableNext()
                }
            }

            ScreenMode.AddSituation -> {
                //Add first fragment
                if (addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(SituationFragment::class.java.simpleName),
                        AddSituationFragment(),
                        true
                    )
                }

                //update toolbar controls
                binding.toolbar.btnForward.visibility = View.VISIBLE
                binding.toolbar.btnNext2.visibility = View.VISIBLE
                binding.toolbar.btnNext2.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.ic_btn_checkoption
                    )
                )
                binding.toolbar.btnClose.visibility = View.GONE
                binding.toolbar.btnPlus.visibility = View.GONE
                binding.toolbar.tvTitle.setText(getString(R.string.add_situation_block))
                setNextEnabled(false)
                //check for selection on backpress
                val fragment =
                    supportFragmentManager.findFragmentByTag(AddSituationFragment::class.java.simpleName)
                if (fragment != null) {
                    (fragment as AddSituationFragment).enableDisableNext()
                }
            }

        }

        UiUtils.hideKeyboard(this)
        //binding.scrollView.smoothScrollTo(0,0)
    }

    private fun onMoodSelected() {
        mScreenMode = ScreenMode.Situation
        updateUI()
    }


    private fun onSituationSelected() {
        /*mScreenMode = ScreenMode.Details
        updateUI()*/

        //Update count
        updateActivityUse()

        //Save record and update UI
        DBManager.instance.saveJournalEntry(mUserEntryInfo)

        //set result
        val intent1 = Intent()
        intent1.putExtra("entry", Gson().toJson(mUserEntryInfo))
        setResult(RESULT_OK, intent1)

        //show toast
        if(mIsInEditMode) {
        }
        else {
            UiUtils.showSuccessToast(this, getString(R.string.mood_entry_created))
        }
        finish()
    }

    private fun onNewActivityAdded() {
        onBackPressed()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (mScreenMode == ScreenMode.Situation) {
            mScreenMode = ScreenMode.Mood
        } else if (mScreenMode == ScreenMode.AddSituation) {
            mScreenMode = ScreenMode.Situation
        }

        updateUI(false)
    }

    fun getUserActivityRecentlyUsedList() = mUserActivityFrequentList


    fun updateActivityUse() {
        val list = ArrayList<String>()
        for (item in mUserEntryInfo.activities) {
            list.add(item.activityId)
        }

        DBManager.instance.updateActivityUsageCount(list)
    }
}