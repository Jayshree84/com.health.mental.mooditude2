package com.health.mental.mooditude.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.View
import android.widget.TextView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.listener.FreshChatListener
import com.health.mental.mooditude.services.freshchat.ChatService

class M3AssessmentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m3assessment)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        val tvTitle = findViewById<TextView>(R.id.tv_title)
        if (Build.VERSION.SDK_INT >= 24) {
            tvTitle.setText(Html.fromHtml(getString(R.string.to_begin_with), 0))
        } else {
            tvTitle.setText(Html.fromHtml(getString(R.string.to_begin_with)))
        }

        val tvDesc = findViewById<TextView>(R.id.tv_desc)
        if (Build.VERSION.SDK_INT >= 24) {
            tvDesc.setText(Html.fromHtml(getString(R.string.get_a_clear_picture), 0))
        } else {
            tvDesc.setText(Html.fromHtml(getString(R.string.get_a_clear_picture)))
        }
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

    fun onNextBtnClicked(view: View) {
        startM3Assessment(true)
        //finish()
    }

    fun onSkipBtnClicked(view: View) {
        SharedPreferenceManager.setAssessmentCompleted(true)
        showHomePage()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatService.instance.removeUnreadCountListener(mListener)
    }
}