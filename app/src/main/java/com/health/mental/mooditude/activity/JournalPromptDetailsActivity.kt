package com.health.mental.mooditude.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.custom.SimpleGestureFilter
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.entity.JournalPrompt
import com.health.mental.mooditude.data.model.journal.EntryAttachmentType
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.data.model.journal.JournalPromptStep
import com.health.mental.mooditude.databinding.ActivityJournalPromptDetailsBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.journal.JournalStepFragment
import com.health.mental.mooditude.fragment.journal.JournalStepInputFragment
import com.health.mental.mooditude.fragment.journal.MoodFragment
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.startedGuidedJournaling
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.UiUtils.loadImage
import java.lang.reflect.Type
import java.util.*


class JournalPromptDetailsActivity : BaseActivity(), SimpleGestureFilter.SimpleGestureListener {


    private lateinit var detector: SimpleGestureFilter
    private lateinit var binding: ActivityJournalPromptDetailsBinding
    private var listSteps: ArrayList<JournalPromptStep>? = null
    private var mCurrPos = 0
    private lateinit var mJournalPrompt: JournalPrompt
    private var mCurrFragmentTag: String = ""
    private var mIsInEditMode: Boolean = false
    private var moveToNextStepAuto = false
    private var mUserEntry: Entry = Entry()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJournalPromptDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        // Detect touched area
        // Detect touched area
        detector = SimpleGestureFilter(this, this)
    }

    override fun initComponents() {

        //Check if it's in edit mode
        if (intent.extras != null && intent.extras!!.containsKey("edit_mode")) {
            mIsInEditMode = intent.extras!!.getBoolean("edit_mode", false)
            val entry = intent.extras!!.getString("entry")
            if (entry != null) {
                this.mUserEntry = Gson().fromJson(entry, Entry::class.java)

                mJournalPrompt =
                    JournalPrompt.readFromApi(JournalPrompt.getPromptFromUserInfo(mUserEntry.userInfo!!))!!
            }
        } else if (intent.extras != null && intent.extras!!.containsKey("prompt")) {

            val objStr = intent.extras!!.getString("prompt")
            val objType: Type = object : com.google.gson.reflect.TypeToken<JournalPrompt?>() {}.type
            mJournalPrompt = Gson().fromJson(objStr, objType)
        }

        loadImage(this, mJournalPrompt.imgStr, binding.ivImage)

        debugLog(TAG, "Step String : " + mJournalPrompt.stepsStr)
        listSteps = JournalPrompt.getJournalPromptSteps(mJournalPrompt.stepsStr!!)
        debugLog(TAG, " steps size : " + listSteps!!.size)

        binding.toolbar.progress.visibility = View.GONE
        //enable/disable - back n next
        binding.toolbar.btnClose.setOnClickListener {
            //setResult(RESULT_OK)
            finish()
        }
        binding.toolbar.btnForward.setOnClickListener {
            mCurrPos--

            enableNavigationButtons()
            onBackPressed()

            val prompt = listSteps!!.get(mCurrPos)
            //Change current fragment as per prompt
            if (prompt.input) {
                mCurrFragmentTag = JournalStepInputFragment::class.java.simpleName
                val fragment = supportFragmentManager.findFragmentByTag(mCurrFragmentTag)
                if (fragment != null) {
                    (fragment as JournalStepInputFragment).enableDisableNext()
                }
            } else {
                mCurrFragmentTag = JournalStepFragment::class.java.simpleName
                setNextEnabled(true)
            }
            debugLog(
                TAG,
                "Prompt Input : " + prompt.input + " : " + mCurrPos + " : " + mCurrFragmentTag
            )
        }
        binding.toolbar.btnNext2.setOnClickListener {

            //Get current step and check for input if input is required then save it to steps's userInput field

            val currFragment = supportFragmentManager.findFragmentByTag(mCurrFragmentTag)
            if (currFragment is JournalStepInputFragment) {
                val textInput = (currFragment as JournalStepInputFragment).getUserInput()
                if (textInput.trim().isEmpty()) {
                    UiUtils.showErrorToast(this, getString(R.string.please_enter_text))
                    return@setOnClickListener
                } else {
                    //update userinput
                    val prompt = listSteps!!.get(mCurrPos)
                    prompt.userInput = textInput
                    UiUtils.hideKeyboard(this)
                }
            } else if (currFragment is MoodFragment) {
                val success = currFragment.updateMoodValues(mUserEntry)
                if (success) {
                    saveEntry()
                }

                return@setOnClickListener
            }

            //Update UI
            onNextPressed()
            /*if(moveToNextStepAuto) {
                moveToInputStep()
            }*/
        }

        //Add first fragment
        val prompt = listSteps!!.get(0)
        if (prompt.input) {
            val fragment = JournalStepInputFragment.newInstance(prompt)
            addFragment(R.id.layout_container, fragment, true)
            mCurrFragmentTag = JournalStepInputFragment::class.java.simpleName
            setNextEnabled(false)
        } else {
            val fragment = JournalStepFragment.newInstance(prompt)
            addFragment(R.id.layout_container, fragment, true)
            mCurrFragmentTag = JournalStepFragment::class.java.simpleName
        }
        //Enable/disable buttons
        enableNavigationButtons()

        EventCatalog.instance.startedGuidedJournaling(mJournalPrompt,
            0.0
        )

        //Now if it's in edit mode then move to input step
        //moveToInputStep()

        //call next on click/tap
        binding.layoutContainer.setOnClickListener {
            onSwipe(SimpleGestureFilter.SWIPE_LEFT)
        }
    }

    private fun moveToInputStepOld() {
        if (mIsInEditMode) {
            if (binding.toolbar.btnNext2.isEnabled) {
                if (mCurrPos >= (listSteps!!.size)) {
                    //stop here
                    moveToNextStepAuto = false
                } else {
                    val prompt = listSteps!!.get(mCurrPos)
                    //Change current fragment as per prompt
                    if (prompt.input) {
                        //stop here
                        moveToNextStepAuto = false
                    } else {
                        moveToNextStepAuto = true
                        binding.toolbar.btnNext2.callOnClick()
                    }
                }
            }
        }
    }


    private fun onNextPressed() {

        mCurrPos++
        debugLog(TAG, "Pos : " + mCurrPos + " : " + listSteps!!.size + " : " + mCurrFragmentTag)

        //check for last step
        if (mCurrPos >= (listSteps!!.size)) {

            var fragment = MoodFragment()
            if(mUserEntry.emotion != null) {
                fragment = MoodFragment.newInstance(
                    mUserEntry.emotion!!,
                    mUserEntry.emotionIntensity,
                    mUserEntry.postedDate
                )
            }

            //show mood fragment and change icon
            addFragment(
                R.id.layout_container,
                supportFragmentManager.findFragmentByTag(mCurrFragmentTag),
                fragment,
                true
            )
            mCurrFragmentTag = MoodFragment::class.java.simpleName
            binding.toolbar.btnNext2.setImageResource(R.drawable.ic_btn_checkoption)
            setNextEnabled(false)

            //step completed
            EventCatalog.instance.startedGuidedJournaling(mJournalPrompt, 1.0)

        } else {
            //I found null when mistake in server's data , so keep null checking
            if (listSteps!!.get(mCurrPos) == null) {
                mCurrPos++
            }

            val prompt = listSteps!!.get(mCurrPos)

            enableNavigationButtons()
            //now add fragment for email/password
            if (prompt.input) {
                addFragment(
                    R.id.layout_container,
                    supportFragmentManager.findFragmentByTag(mCurrFragmentTag),
                    JournalStepInputFragment.newInstance(prompt),
                    true
                )
                mCurrFragmentTag = JournalStepInputFragment::class.java.simpleName
                setNextEnabled(false)

            } else {
                addFragment(
                    R.id.layout_container,
                    supportFragmentManager.findFragmentByTag(mCurrFragmentTag),
                    JournalStepFragment.newInstance(prompt),
                    true
                )
                mCurrFragmentTag = JournalStepFragment::class.java.simpleName
            }
            //log an event
            val totalSteps = listSteps!!.size
            val currentStep = mCurrPos
            EventCatalog.instance.startedGuidedJournaling(mJournalPrompt,
                (currentStep.toDouble()/totalSteps.toDouble())
            )
        }
    }

    private fun enableNavigationButtons() {
        if (mCurrPos < (listSteps!!.size)) {
            setNextEnabled(true)
            binding.toolbar.btnNext2.setImageResource(R.drawable.ic_btn_next)
        } else {
            //setNextEnabled(false)
            setNextEnabled(true)
            binding.toolbar.btnNext2.setImageResource(R.drawable.ic_btn_checkoption)
        }
        if (mCurrPos > 0) {
            setForwardEnabled(true)
        } else {
            setForwardEnabled(false)
        }
    }

    /**
     * Enables/disables NEXT button
     */
    fun setNextEnabled(flag: Boolean) {
        binding.toolbar.btnNext2.isEnabled = flag
    }

    /**
     * Enables/disables FORWARD button
     */
    fun setForwardEnabled(flag: Boolean) {
        binding.toolbar.btnForward.isEnabled = flag
    }


    private fun saveEntry() {

        val entry = mUserEntry
        if (mIsInEditMode) {
            entry.modifiedOn = Date(System.currentTimeMillis())
        } else {
            entry.postedDate = Date(System.currentTimeMillis())
            entry.entryType = EntryType.guidedJournal
            entry.attachmentType = EntryAttachmentType.guidedJournal
        }
        //update steps string
        mJournalPrompt.stepsStr = JournalPrompt.getJournalPromptStepString(listSteps!!)
        entry.userInfo = JournalPrompt.getUserinfo(mJournalPrompt)

        //Save record and update UI
        DBManager.instance.saveJournalEntry(entry)

        //set result
        val intent1 = Intent()
        intent1.putExtra("entry", Gson().toJson(mUserEntry))
        setResult(RESULT_OK, intent1)

        //show toast
        if (mIsInEditMode) {

        } else {
            //show toast
            UiUtils.showSuccessToast(this, getString(R.string.entry_added_successfully))
        }
        finish()
    }

    override fun dispatchTouchEvent(me: MotionEvent?): Boolean {
        // Call onTouchEvent of SimpleGestureFilter class
        detector.onTouchEvent(me!!)
        return super.dispatchTouchEvent(me)
    }

    override fun onSwipe(direction: Int) {

        debugLog(TAG, "OnSwipe :: " + direction)
        //Detect the swipe gestures and display toast
        when (direction) {
            SimpleGestureFilter.SWIPE_RIGHT -> if (binding.toolbar.btnForward.isEnabled) binding.toolbar.btnForward.callOnClick()
            SimpleGestureFilter.SWIPE_LEFT -> if (binding.toolbar.btnNext2.isEnabled) binding.toolbar.btnNext2.callOnClick()
            SimpleGestureFilter.SWIPE_DOWN -> {
            }
            SimpleGestureFilter.SWIPE_UP -> {
            }
        }
        //Toast.makeText(this, showToastMessage, Toast.LENGTH_SHORT).show()
    }


    //Toast shown when double tapped on screen
    override fun onDoubleTap() {
        /*Toast.makeText(this, "You have Double Tapped.", Toast.LENGTH_SHORT)
            .show()*/
    }

}