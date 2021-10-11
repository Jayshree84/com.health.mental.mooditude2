package com.health.mental.mooditude.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.fragment.InformationDlgFragment

class M3AssessmentStartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_massessment_start)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))

        val tvTitle = findViewById<TextView>(R.id.tv_title)
        if (Build.VERSION.SDK_INT >= 24) {
            tvTitle.setText(Html.fromHtml(getString(R.string.assess_your_score), 0))
        } else {
            tvTitle.setText(Html.fromHtml(getString(R.string.assess_your_score)))
        }

        val tvDesc = findViewById<TextView>(R.id.tv_desc)
        if (Build.VERSION.SDK_INT >= 24) {
            tvDesc.setText(Html.fromHtml(getString(R.string.read_each_statement), 0))
        } else {
            tvDesc.setText(Html.fromHtml(getString(R.string.read_each_statement)))
        }
    }

    fun onStartBtnClicked(view: View) {
        startM3AssessmentQuestions(intent.extras!!.getBoolean("show_home"))
    }


    fun onInfoBtnClicked(view: View) {
        /*val intent1 = Intent(this, CustomDialogActivity::class.java)
        startActivity(intent1)*/

        supportFragmentManager.let {
            InformationDlgFragment().apply {
                show(it, tag)
            }
        }

    }
}