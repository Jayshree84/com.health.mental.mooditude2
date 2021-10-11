package com.health.mental.mooditude.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.View
import android.widget.TextView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.services.freshchat.ChatService
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.listener.FreshChatListener
import org.jetbrains.anko.alert

class WelcomeUserActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_user)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        val tvWelcomeUser = findViewById<TextView>(R.id.tv_welcome)
        val user = DataHolder.instance.getCurrentUser()
        if (user == null) {
            alert("Something went wrong, Try again....").show()
            return
        }
        tvWelcomeUser.text = String.format(getString(R.string.welcome_name), user.name)

        val tvWelcomeText = findViewById<TextView>(R.id.tv_you_taken)
        if (Build.VERSION.SDK_INT >= 24) {
            tvWelcomeText.setText(Html.fromHtml(getString(R.string.you_taken), 0))
        } else {
            tvWelcomeText.setText(Html.fromHtml(getString(R.string.you_taken)))
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

    fun onContinueBtnClicked(view: View) {
        //User is created first time so let's set his profile
        setUserProfileSettings()
        //finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatService.instance.removeUnreadCountListener(mListener)
    }

}