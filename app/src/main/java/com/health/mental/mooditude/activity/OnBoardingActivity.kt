package com.health.mental.mooditude.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.services.freshchat.ChatService
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.onboardingsignup.*
import com.health.mental.mooditude.listener.FreshChatListener
import com.health.mental.mooditude.services.instrumentation.*
import com.health.mental.mooditude.utils.REQUEST_CREATE_GUIDED_ENTRY
import com.health.mental.mooditude.utils.REQUEST_CREATE_MOOD_ENTRY
import com.health.mental.mooditude.utils.REQUEST_ID_START_REGISTRATION


class OnBoardingActivity : BaseActivity() {

    private var mainMenu:Menu?  = null
    private val mListener = object:FreshChatListener {
        override fun getUnreadCountReceived(unreadCount: Int) {
            //debugLog(TAG, "listener called mainmenu : " + mainMenu + " : " + unreadCount)
            if(mainMenu != null) {
                showUnreadMsgCount(mainMenu!!, unreadCount)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        initComponents()
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

    override fun initComponents() {
        //add fragment
        initActionBar(findViewById(R.id.toolbar))

        //add first fragment
        val fragment = DeliverResultFragment()
        addFragment(R.id.layout_container, fragment, true)
        EventCatalog.instance.onboardingStep("Welcome")
    }

    //First step
    fun onShowMeBtnClicked() {
        //add next fragment
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(DeliverResultFragment::class.java.simpleName),
            DailyCheckInFragment(),
            true
        )
        EventCatalog.instance.onboardingStep("Mood")
    }


    fun onCheckInBtnClicked() {
        //launch activity to log mood
        val intent = Intent(this, HowYouFeelActivity::class.java)
        startActivityForResult(REQUEST_CREATE_MOOD_ENTRY, intent)

        //Mood tracking
        EventCatalog.instance.onboarding_MoodTracking()
    }

    fun onSkipFromDailyCheckIn() {
        //add next fragment
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(DailyCheckInFragment::class.java.simpleName),
            RegulateEmotionsFragment(),
            true
        )
        EventCatalog.instance.onboardingStep("Emotions")
    }

    fun onSkipFromRegulateEmotions() {
        //add next fragment
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(RegulateEmotionsFragment::class.java.simpleName),
            GuidanceFragment(),
            true
        )
        EventCatalog.instance.onboardingStep("Guidance")
    }

    fun onMoodEntryCreated(data: Intent?) {
        //Replace the fragment with new fragment
        replaceFragment(R.id.layout_container,
            ThumbsUpFragment())
    }

    fun onGuidedEntryCreated(data: Intent?) {
        //Replace the fragment with new fragment
        replaceFragment(R.id.layout_container,
            ExcellentFragment())
    }

    fun onThumbsUpContinueBtnClicked() {
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(ThumbsUpFragment::class.java.simpleName),
            RegulateEmotionsFragment(),
            true
        )
    }

    fun onSignupBtnClicked() {
        val intent1 = Intent(this, EmailRegistrationActivity::class.java)
        startActivityForResult(REQUEST_ID_START_REGISTRATION, intent1)
        overridePendingTransition(R.anim.anim_slide_in_bottom, R.anim.anim_slide_out_top)
        //setResult(RESULT_OK)
        //finish()
    }

    fun onJoinMooditudeBtnClicked() {
        onSignupBtnClicked()
        EventCatalog.instance.completedOnboarding()
    }

    fun onGuidedEntryBtnClicked() {
        //launch activity to log mood
        val intent = Intent(this, JournalPromptDetailsActivity::class.java)
        val prompt = DBManager.instance.getPromptForOnboard()
        intent.putExtra("prompt", Gson().toJson(prompt))
        startActivityForResult(REQUEST_CREATE_GUIDED_ENTRY, intent)

        EventCatalog.instance.onboarding_Journaling()
    }

    fun onExcellentContinueBtnClicked() {
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(ExcellentFragment::class.java.simpleName),
            GuidanceFragment(),
            true
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatService.instance.removeUnreadCountListener(mListener)
    }

}