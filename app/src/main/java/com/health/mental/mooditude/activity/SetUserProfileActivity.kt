package com.health.mental.mooditude.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.model.UserTopGoal
import com.health.mental.mooditude.data.model.journal.EntryAttachmentType
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.databinding.ActivitySetUserProfileBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.onboarding.*
import com.health.mental.mooditude.listener.FreshChatListener
import com.health.mental.mooditude.services.freshchat.ChatService
import java.util.*

class SetUserProfileActivity : BaseActivity() {

    private enum class ScreenMode {
        Age,
        Gender,
        Goal,
        UserChallenges,
        HealthProfessional,
        CBT,
        MakePromise,
        Remind,
        Theme,
    }

    //Show age first
    private var mScreenMode: ScreenMode = ScreenMode.Age
    private lateinit var binding: ActivitySetUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActionBar(findViewById(R.id.toolbar))
        initComponents()
    }

    override fun initComponents() {
        val fragment = AgeFragment()
        addFragment(R.id.layout_container, fragment, true)
    }

    private var mainMenu:Menu?  = null
    private val mListener = object: FreshChatListener {
        override fun getUnreadCountReceived(unreadCount: Int) {
            //debugLog(TAG, "listener called mainmenu : " + mainMenu + " : " + unreadCount)
            if(mainMenu != null) {
                showUnreadMsgCount(mainMenu!!, unreadCount)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.help_options, menu)
        this.mainMenu = menu
        debugLog(TAG, "mainmenu : " + this.mainMenu)
        //check for unread message count
        ChatService.instance.getUnReadCount(mListener)
        ChatService.instance.addUnreadCountListener(mListener)
        return true
    }

    fun onAgeSelected(value: Any?) {

        val selectedAge = value as Int
        //update on firebase
        DBManager.instance.updateUserAgeGroup(selectedAge)

        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(AgeFragment::class.java.simpleName),
            GenderFragment(),
            true
        )
        mScreenMode = ScreenMode.Gender
    }

    fun onGenderSelected(value: Any?) {
        val selection = value as Int
        //update on firebase
        DBManager.instance.updateGender(selection)

        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(GenderFragment::class.java.simpleName),
            GoalFragment(),
            true
        )
        mScreenMode = ScreenMode.Goal
    }

    fun onGoalSelected(value: Any?) {

        val selection = value as UserTopGoal
        //update on firebase
        DBManager.instance.updateUserGoal(selection)

        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(GoalFragment::class.java.simpleName),
            ChallengesFragment(),
            true
        )
        mScreenMode = ScreenMode.UserChallenges
    }

    fun onChallengesSelected(value: Any?) {
        val selection = value as String
        //update on firebase
        DBManager.instance.updateUserChallenges(selection)
        debugLog(TAG, "challenges selected : " + value.toString())
        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(ChallengesFragment::class.java.simpleName),
            HealthProfessionalFragment(),
            true
        )
        mScreenMode = ScreenMode.HealthProfessional
    }

    fun onHealthProfessionalSelected(selection: Boolean) {
        //update on firebase
        DBManager.instance.updateHealthProfessional(selection)

        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(HealthProfessionalFragment::class.java.simpleName),
            CBTFragment(),
            true
        )
        mScreenMode = ScreenMode.CBT
    }

    fun onCBTSelected(selection: Boolean) {
        //update on firebase
        DBManager.instance.updateCBT(selection)

        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(CBTFragment::class.java.simpleName),
            MakePromiseFragment(),
            true
        )
        mScreenMode = ScreenMode.MakePromise
    }

    fun onMakePromiseSelected(selection: Boolean) {
        //update on firebase
        DBManager.instance.updateMakePromise(selection)

        //If commited then add entry - unguided
        if(selection) {
            val entry = Entry()
            entry.post = getString(R.string.make_promise_entry_post)
            entry.postedDate = Date(System.currentTimeMillis())
            entry.entryType = EntryType.journal
            entry.attachmentType = EntryAttachmentType.journal
            //Save record and update UI
            DBManager.instance.saveJournalEntry(entry)
        }

        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(MakePromiseFragment::class.java.simpleName),
            RemindFragment(),
            true
        )
        mScreenMode = ScreenMode.Remind
    }

    fun onReminderSelected(selection: Boolean) {
        //update on firebase
        DBManager.instance.updateReminder(selection)

        /*//now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(RemindFragment::class.java.simpleName),
            SelectThemeFragment(),
            true
        )
        mScreenMode = ScreenMode.Theme
    }

    fun onThemeSelected(selection: Boolean) {
        //Selected theme*/

        //update on firebase
        DBManager.instance.profileCompleted(true)

        //now start Boarding complete activity
        val intent1 = Intent(this, ProfileCompletedActivity::class.java)
        startActivity(intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        setResult(RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatService.instance.removeUnreadCountListener(mListener)
    }
}