package com.health.mental.mooditude.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.ui.tracking.FullReportFragment
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.databinding.ActivityM3AssessmentScoreFullBinding
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.tookAssessment

class M3AssessmentScoreFullActivity : BaseActivity() {

    private lateinit var binding: ActivityM3AssessmentScoreFullBinding
    //private var mM3Assessment:com.health.mental.mooditude.data.entity.M3Assessment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityM3AssessmentScoreFullBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fetch from bundle
        //mM3Assessment = intent.extras!!.get("assessment") as com.health.mental.mooditude.data.entity.M3Assessment?
        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))

        val tvTitle = findViewById<TextView>(R.id.toolbar_title)
        tvTitle.setText(R.string.your_score)
        val m3Assessment = M3AssessmentManager.makeCurrentAssessment()

        addFragment(R.id.layout_container, FullReportFragment.newInstance(m3Assessment, 0), true)

        //Save this assessment data on firebase
        M3AssessmentManager.saveAssessment(m3Assessment)
        SharedPreferenceManager.setAssessmentCompleted(true)

        //log event
        EventCatalog.instance.tookAssessment(m3Assessment, "")

        //check for home page
        binding.btnStart.setOnClickListener {
            showHomePage()
        }
        val showHome = intent.extras!!.getBoolean("show_home")
        if(showHome) {
            binding.btnStart.visibility = View.VISIBLE
            //do not show close btn
            //supportActionBar!!.setDisplayHomeAsUpEnabled(false);
            //supportActionBar!!.setHomeButtonEnabled(false);
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            tvTitle.gravity = Gravity.CENTER
        }
        else {
            binding.btnStart.visibility = View.GONE
        }
    }
}

