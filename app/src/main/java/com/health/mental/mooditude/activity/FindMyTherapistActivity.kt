package com.health.mental.mooditude.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.ui.care.MainFragment
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.data.model.ApiTherapistRequest
import com.health.mental.mooditude.data.model.TherapistRequestInfo
import com.health.mental.mooditude.databinding.ActivityFindMyTherapistBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.requesttherapist.*
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.openedRequestTherapist
import com.health.mental.mooditude.services.instrumentation.sendRequestTherapist
import com.health.mental.mooditude.services.instrumentation.therapistRequestStep
import com.health.mental.mooditude.utils.UiUtils
import org.jetbrains.anko.indeterminateProgressDialog
import java.util.*

class FindMyTherapistActivity : BaseActivity() {

    private var mLatestAssessment: M3Assessment? = null
    private lateinit var binding: ActivityFindMyTherapistBinding
    private val mRequestInfo = TherapistRequestInfo()

    enum class ScreenMode {
        HealthTherapists ,
        HasAssessment,
        NoAssessment,
        AssessmentExpired,
        TherapistPreference,
        ProviderAttributes,
        UserPersonalInfo,
        ContactInfo,
        PaymentMethod,
        Comment,
        Consent


        /*{
            public override fun next():ScreenMode?{
                return null; // see below for options for this line
            };
        };

        public open fun next():ScreenMode? {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal + 1];
        }

        public open fun prev():ScreenMode? {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal - 1];
        }*/
    }

    private var mScreenMode = ScreenMode.HealthTherapists
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFindMyTherapistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        //log an event
        EventCatalog.instance.openedRequestTherapist()
    }

    override fun initComponents() {
        val lastestAssessment = DBManager.instance.getLastestAssessment()
        lastestAssessment.observe(this, Observer {
            debugLog(TAG, "Observer called : " + it)
            mLatestAssessment = it
            //new assessment is received, update appropriate fragments
            updateUiOnNewAssessment()
        })

        //set title
        //binding.toolbar1.tvTitle.text = getString(R.string.find_my_therapist)
        binding.toolbar1.progress.scaleY = 3f
        binding.toolbar1.progress.max = 10

        //enable/disable - back n next
        binding.toolbar1.btnClose.setOnClickListener {
            finish()
        }
        binding.toolbar1.btnForward.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar1.btnNext2.setOnClickListener {
            when(mScreenMode) {
                ScreenMode.HealthTherapists -> {
                    onFindTherapistBtnClicked()
                }
                ScreenMode.HasAssessment -> {
                    onUseThisAssessmentBtnClicked()
                }
                ScreenMode.TherapistPreference -> {
                    //fetch preferences list by order
                    val fragment = supportFragmentManager.findFragmentByTag(
                        TherapistPreferenceFragment::class.java.simpleName)
                    if(fragment != null) {
                        val list = (fragment as TherapistPreferenceFragment).getPreferences()
                        mRequestInfo.listPreferences.clear()
                        for(item in list) {
                            mRequestInfo.listPreferences.add(item.shortText)
                        }
                    }

                    onTherapistPreferencesSelected()
                }
                ScreenMode.ProviderAttributes -> {
                    //fetch provider attributes
                    val fragment = supportFragmentManager.findFragmentByTag(
                        ProviderAttributesFragment::class.java.simpleName)
                    if(fragment != null) {
                        mRequestInfo.providerAttributes = (fragment as ProviderAttributesFragment).getTherapistProviderAttributes()

                        debugLog(TAG,"providerAttributes : " + mRequestInfo.providerAttributes.ageGroup + " : " +
                                    mRequestInfo.providerAttributes.ethnicity + " : " +
                                mRequestInfo.providerAttributes.gender + " : " +
                                mRequestInfo.providerAttributes.language + " : " +
                                mRequestInfo.providerAttributes.religion               )
                    }

                    onProviderAttributesSelected()

                }
                ScreenMode.UserPersonalInfo -> {
                    //fetch provider attributes
                    val fragment = supportFragmentManager.findFragmentByTag(UserPersonalInfoFragment::class.java.simpleName)
                    if(fragment != null) {
                        val success =
                            (fragment as UserPersonalInfoFragment).updateUserAttributes(mRequestInfo)

                        if(success) {
                            onUserPersonalInfoSelected()
                        }
                    }
                }
                ScreenMode.ContactInfo -> {
                    //fetch provider attributes
                    val fragment = supportFragmentManager.findFragmentByTag(UserContactInfoFragment::class.java.simpleName)
                    if(fragment != null) {
                        val success =
                            (fragment as UserContactInfoFragment).updateContactInfo(mRequestInfo)
                        debugLog(TAG,"providerAttributes : " + mRequestInfo.bestTimeToContact.toString())

                        if(success) {
                            onUserContactInfoSelected()
                        }
                    }
                }
                ScreenMode.PaymentMethod -> {
                    //fetch provider attributes
                    val fragment = supportFragmentManager.findFragmentByTag(PaymentMethodFragment::class.java.simpleName)
                    if(fragment != null) {
                        val success =
                            (fragment as PaymentMethodFragment).updatePaymentMethod(mRequestInfo)
                        debugLog(TAG,"providerAttributes : " + mRequestInfo.paymentMethod.toString())

                        if(success) {
                            onUserPaymentMethodSelected()
                        }
                    }
                }
                ScreenMode.Comment -> {
                    //fetch provider attributes
                    val fragment = supportFragmentManager.findFragmentByTag(RequestCommentFragment::class.java.simpleName)
                    if(fragment != null) {
                        val success =
                            (fragment as RequestCommentFragment).updateCommentMessage(mRequestInfo)

                        if(success) {
                            onCommentAdded()
                        }
                    }
                }


            }
        }

        //First fragment
        mScreenMode = ScreenMode.HealthTherapists
        updateUI()
    }

    private fun updateUiOnNewAssessment() {
        when(mScreenMode) {
            ScreenMode.HasAssessment -> {
                val fragment = supportFragmentManager.findFragmentByTag(CurrentAssessmentFragment::class.java.simpleName)
                if(fragment != null && mLatestAssessment != null) {
                    (fragment as CurrentAssessmentFragment).updateUi(mLatestAssessment!!)
                }
            }
        }
    }

    private fun updateUI(addFragments:Boolean = true) {
        when (mScreenMode) {
            ScreenMode.HealthTherapists -> {
                //Add first fragment
                if(addFragments) {
                    val fragment = HealthTherapistsFragment()
                    addFragment(R.id.layout_container, fragment, true)
                    EventCatalog.instance.therapistRequestStep("HealthTherapists")
                }

                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.GONE
                binding.toolbar1.btnNext2.visibility = View.VISIBLE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 1
            }
            ScreenMode.AssessmentExpired -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(HealthTherapistsFragment::class.java.simpleName),
                        ExpiredAssessmentFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("AssessmentExpired")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.GONE
                binding.toolbar1.btnClose.visibility = View.GONE
                binding.toolbar1.progress.progress = 2
            }
            ScreenMode.NoAssessment -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(MainFragment::class.java.simpleName),
                        NoAssessmentFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("NoAssessment")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.GONE
                binding.toolbar1.btnClose.visibility = View.GONE
                binding.toolbar1.progress.progress = 2
            }
            ScreenMode.HasAssessment -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(HealthTherapistsFragment::class.java.simpleName),
                        CurrentAssessmentFragment.newInstance(mLatestAssessment!!),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("HasAssessment")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.VISIBLE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 2
            }
            ScreenMode.TherapistPreference -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(CurrentAssessmentFragment::class.java.simpleName),
                        TherapistPreferenceFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("Preference")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.VISIBLE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 3
            }
            ScreenMode.ProviderAttributes -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(TherapistPreferenceFragment::class.java.simpleName),
                        ProviderAttributesFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("ProviderAttribute")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.VISIBLE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 4
            }

            ScreenMode.UserPersonalInfo -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(ProviderAttributesFragment::class.java.simpleName),
                        UserPersonalInfoFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("PersonalInfo")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.VISIBLE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 5
            }
            ScreenMode.ContactInfo -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(UserPersonalInfoFragment::class.java.simpleName),
                        UserContactInfoFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("ContactInfo")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.VISIBLE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 6
            }
            ScreenMode.PaymentMethod -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(UserContactInfoFragment::class.java.simpleName),
                        PaymentMethodFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("Payment")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.VISIBLE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 7
            }
            ScreenMode.Comment -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(PaymentMethodFragment::class.java.simpleName),
                        RequestCommentFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("Comment")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.VISIBLE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 8
            }
            ScreenMode.Consent -> {
                if(addFragments) {
                    addFragment(
                        R.id.layout_container,
                        supportFragmentManager.findFragmentByTag(RequestCommentFragment::class.java.simpleName),
                        RequestConsentFragment(),
                        true
                    )
                    EventCatalog.instance.therapistRequestStep("Consent")
                }
                //update toolbar controls
                binding.toolbar1.btnForward.visibility = View.VISIBLE
                binding.toolbar1.btnNext2.visibility = View.GONE
                binding.toolbar1.btnClose.visibility = View.VISIBLE
                binding.toolbar1.progress.progress = 9
            }
        }

        binding.scrollView.smoothScrollTo(0,0)
        UiUtils.hideKeyboard(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //change screen mode
        if(mScreenMode == ScreenMode.HasAssessment || mScreenMode == ScreenMode.AssessmentExpired ||
                mScreenMode == ScreenMode.NoAssessment) {
            mScreenMode = ScreenMode.HealthTherapists
        }
        else if(mScreenMode == ScreenMode.TherapistPreference) {
            mScreenMode = ScreenMode.HasAssessment
        }
        else if(mScreenMode == ScreenMode.ProviderAttributes) {
            mScreenMode = ScreenMode.TherapistPreference
        }
        else if(mScreenMode == ScreenMode.UserPersonalInfo) {
            mScreenMode = ScreenMode.ProviderAttributes
        }
        else if(mScreenMode == ScreenMode.ContactInfo) {
            mScreenMode = ScreenMode.UserPersonalInfo
        }
        else if(mScreenMode == ScreenMode.PaymentMethod) {
            mScreenMode = ScreenMode.ContactInfo
        }
        else if(mScreenMode == ScreenMode.Comment) {
            mScreenMode = ScreenMode.PaymentMethod
        }
        else if(mScreenMode == ScreenMode.Consent) {
            mScreenMode = ScreenMode.Comment
        }
        updateUI(false)

    }

    fun onFindTherapistBtnClicked() {
        //Let's first check if we have any assessment
        if (mLatestAssessment != null) {
            //now check for expiry
            if (M3AssessmentManager.isAssessmentExpired(mLatestAssessment!!)) {
                mScreenMode = ScreenMode.AssessmentExpired
            } else {
                mScreenMode = ScreenMode.HasAssessment
            }
        } else {
            mScreenMode = ScreenMode.NoAssessment
        }
        updateUI()
    }

    fun onUseThisAssessmentBtnClicked() {
        //update requestinfo
        mRequestInfo.assessmentScore = mLatestAssessment!!.allScore
        mScreenMode = ScreenMode.TherapistPreference
        updateUI()
    }

    private fun onTherapistPreferencesSelected() {
        mScreenMode = ScreenMode.ProviderAttributes
        updateUI()
    }

    private fun onProviderAttributesSelected() {
        mScreenMode = ScreenMode.UserPersonalInfo
        updateUI()
    }

    private fun onUserPersonalInfoSelected() {
        mScreenMode = ScreenMode.ContactInfo
        updateUI()
    }

    private fun onUserContactInfoSelected() {
        mScreenMode = ScreenMode.PaymentMethod
        updateUI()
    }

    private fun onUserPaymentMethodSelected() {
        mScreenMode = ScreenMode.Comment
        updateUI()
    }

    private fun onCommentAdded() {
        mScreenMode = ScreenMode.Consent
        updateUI()
    }

    fun onSubmitRequestBtnClicked() {
            //submit request
        //create request and submit it
        val request = ApiTherapistRequest()
        request.postedDate = Date(System.currentTimeMillis())
        request.requestInfo = mRequestInfo

        //show progress
        val dlg = indeterminateProgressDialog(getString(R.string.sending_request))
        DBManager.instance.uploadTherapistRequest(request, object :FBQueryCompletedListener {
            override fun onResultReceived(result: Any?) {

                dlg.dismiss()
                if(result != null) {
                    if(result.equals("success")) {
                        //finish this activity
                        finish()
                        //log an event
                        EventCatalog.instance.sendRequestTherapist(request)
                    }
                    else {
                        //show error msg
                        UiUtils.showErrorToast(this@FindMyTherapistActivity, "Error : " + result.toString())
                    }
                }
            }
        })

    }




}