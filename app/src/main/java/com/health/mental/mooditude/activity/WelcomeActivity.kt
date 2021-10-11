package com.health.mental.mooditude.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.health.mental.mooditude.R
import com.health.mental.mooditude.utils.REQUEST_SIGNUP_ONBOARDING

class WelcomeActivity : BaseActivity() {

    override fun initComponents() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }

    fun onContinueBtnClicked(view: View) {
        showSignUpPage()
    }

    /**
     * Show welcome page to sign-up/sign-in
     */
    private fun showSignUpPage() {
        /*
        val intent1 = Intent(this, EmailRegistrationActivity::class.java)
        startActivityForResult(REQUEST_ID_START_REGISTRATION, intent1)
        overridePendingTransition(R.anim.anim_slide_out_top, R.anim.anim_slide_in_bottom)
        //finish()
         */

        startActivityForResult(REQUEST_SIGNUP_ONBOARDING, Intent(this, OnBoardingActivity::class.java))
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        //finish()
    }
}