package com.health.mental.mooditude.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.health.mental.mooditude.R
import com.health.mental.mooditude.fragment.preferences.DeleteAccountFragment
import com.health.mental.mooditude.fragment.preferences.PreferencesMainFragment
import com.health.mental.mooditude.utils.UiUtils

class PreferencesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_preferences)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))

        setPageTitle(findViewById(R.id.toolbar), getString(R.string.title_preferences))

        val fragment = PreferencesMainFragment()
        addFragment(R.id.layout_container, fragment, true)
        findViewById<View>(R.id.scroll_view).setBackgroundColor(ContextCompat.getColor(this, R.color.home_background))
    }

    fun onDeleteAccountBtnClicked() {
        //now add fragment to delete account
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(PreferencesMainFragment::class.java.simpleName),
            DeleteAccountFragment(),
            true
        )
        setPageTitle(findViewById(R.id.toolbar), getString(R.string.delete_account))
        findViewById<View>(R.id.scroll_view).setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    }


    override fun onBackPressed() {

        val subFragment = supportFragmentManager.findFragmentByTag(DeleteAccountFragment::class.java.simpleName)
        if(subFragment != null) {
            setPageTitle(findViewById(R.id.toolbar), getString(R.string.title_preferences))
            UiUtils.hideKeyboard(this)
            findViewById<View>(R.id.scroll_view).setBackgroundColor(ContextCompat.getColor(this, R.color.home_background))
        }
        super.onBackPressed()
    }

    fun showError(e:Exception) {
        val fragment = supportFragmentManager.findFragmentByTag(DeleteAccountFragment::class.java.simpleName)
        if(fragment != null) {
            (fragment as DeleteAccountFragment).showError(e)
        }
    }


}