package com.health.mental.mooditude.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.ui.community.PostDetailsFragment
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.data.model.journal.JournalPromptStep
import com.health.mental.mooditude.fragment.journal.DetailsFragment
import com.health.mental.mooditude.fragment.journal.JournalMainFragment
import com.health.mental.mooditude.fragment.profile.ProfileMainFragment
import com.health.mental.mooditude.fragment.profile.ProfileSubFragment
import com.health.mental.mooditude.utils.UiUtils

class ProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))

        setPageTitle(findViewById(R.id.toolbar), "")
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brand_yellow))
        findViewById<View>(R.id.toolbar).setBackgroundColor(ContextCompat.getColor(this, R.color.brand_yellow))

        val fragment = ProfileMainFragment()
        addFragment(R.id.layout_container, fragment, true)
        UiUtils.hideKeyboard(this)
    }

    fun onProfileBtnClicked() {
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(ProfileMainFragment::class.java.simpleName),
            ProfileSubFragment(),
            true
        )
        setPageTitle(findViewById(R.id.toolbar), getString(R.string.text_profile))
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar))
        findViewById<View>(R.id.toolbar).setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        UiUtils.hideKeyboard(this)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        UiUtils.hideKeyboard(this)
        val subFragment = supportFragmentManager.findFragmentByTag(ProfileSubFragment::class.java.simpleName)
        if(subFragment != null) {
            val success = (subFragment as ProfileSubFragment).updateUserAttributes()
            if(success) {
                super.onBackPressed()
                setPageTitle(findViewById(R.id.toolbar), "")
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.brand_yellow))
                findViewById<View>(R.id.toolbar).setBackgroundColor(ContextCompat.getColor(this, R.color.brand_yellow))
                return
            }
            else {
                return
            }
        }
        val mainFragment = supportFragmentManager.findFragmentByTag(ProfileMainFragment::class.java.simpleName)
        if(mainFragment != null) {
            val success = (mainFragment as ProfileMainFragment).updateUserAttributes()
            if(success) {
                super.onBackPressed()
                return
            }
            else {
                return
            }
        }
    }

    fun updateImage(selectedImage: Uri) {
        val fragment = supportFragmentManager.findFragmentByTag(ProfileMainFragment::class.java.simpleName)
        if(fragment != null) {
            (fragment as ProfileMainFragment).updateImage(selectedImage)
        }
    }

    fun updateImageFromCamera(uri: Uri?) {
        val fragment = supportFragmentManager.findFragmentByTag(ProfileMainFragment::class.java.simpleName)
        if(fragment != null) {
            (fragment as ProfileMainFragment).updateImageFromCamera(uri)
        }
    }

}