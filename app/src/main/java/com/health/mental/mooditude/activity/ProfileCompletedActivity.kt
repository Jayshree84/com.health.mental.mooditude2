package com.health.mental.mooditude.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.DataHolder
import org.jetbrains.anko.alert

class ProfileCompletedActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_completed)

        initComponents()
    }

    override fun initComponents() {
        val tvMakePromise = findViewById<TextView>(R.id.tv_make_promise)
        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            alert("Something went wrong, Try again....").show()
            return
        }
        if(user.committedToSelfhelp) {
            tvMakePromise.visibility = View.VISIBLE
        }
        else {
            tvMakePromise.visibility = View.INVISIBLE
        }
    }

    fun onContinueBtnClicked(view: View) {
        showAssessmentPage()
    }

}