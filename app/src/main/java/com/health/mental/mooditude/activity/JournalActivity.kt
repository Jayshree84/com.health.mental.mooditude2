package com.health.mental.mooditude.activity

import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.ui.community.PostDetailsFragment
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.data.model.journal.JournalPromptStep
import com.health.mental.mooditude.fragment.journal.DetailsFragment
import com.health.mental.mooditude.fragment.journal.JournalMainFragment
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.editedJournalEntry
import com.health.mental.mooditude.services.instrumentation.viewedJournalEntry

class JournalActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_journal)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))

        setPageTitle(findViewById(R.id.toolbar), getString(R.string.title_journal))

        val fragment = JournalMainFragment()
        addFragment(R.id.layout_container, fragment, true)
    }

    fun onEntryClicked(item: Entry) {
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(JournalMainFragment::class.java.simpleName),
            DetailsFragment.newInstance(item),
            true
        )
        //log an event
        EventCatalog.instance.viewedJournalEntry(item)
    }

    fun OnGuidedEntryStepClicked(entry: Entry, step: JournalPromptStep) {

    }

    fun deleteEntry(entry: Entry) {
        val fragment = supportFragmentManager.findFragmentByTag(JournalMainFragment::class.java.simpleName)
        if(fragment != null) {
            (fragment as JournalMainFragment).deleteEntry(entry)
        }
    }

    fun onEditEntryCompleted(data: Intent?) {
        if(data != null && data.extras != null) {
            val entry = Gson().fromJson( data.extras!!.getString("entry"), Entry::class.java)
            val fragment =
                supportFragmentManager.findFragmentByTag(DetailsFragment::class.java.simpleName)
            if (fragment != null) {
                (fragment as DetailsFragment).refresh(entry)
            }

            //also tell list fragment to update this value
            val listFragment = supportFragmentManager.findFragmentByTag(JournalMainFragment::class.java.simpleName)
            if(listFragment != null) {
                (listFragment as JournalMainFragment).updateEntry(entry)
            }

            //log an event
            EventCatalog.instance.editedJournalEntry(entry)
        }
    }

}