package com.health.mental.mooditude.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.ActivityJournalPromptCatDetailsBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.utils.UiUtils.loadImage
import com.health.mental.mooditude.utils.openURL


class JournalPromptCatDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityJournalPromptCatDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJournalPromptCatDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    override fun initComponents() {

        initActionBar(findViewById(R.id.toolbar))

        //update views
        binding.tvTitle.text = intent.extras!!.getString("title")
        binding.tvShortText.text = intent.extras!!.getString("short_text")
        val htmlText = intent.extras!!.getString("desc")
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvDesc.setText(Html.fromHtml(htmlText, 0))
        } else {
            binding.tvDesc.setText(Html.fromHtml(htmlText))
        }

        val attachment = intent.extras!!.getString("attachment")
        if(attachment != null && attachment.trim().isNotEmpty()) {
            val jsonObject : HashMap<String, String>? = Gson().fromJson(attachment, HashMap::class.java) as HashMap<String, String>?
            debugLog(TAG, jsonObject.toString())
            if(jsonObject != null) {
                binding.viewAttachment.root.visibility = View.VISIBLE

                //name
                binding.viewAttachment.tvName.setText(jsonObject.get("name"))
                binding.viewAttachment.tvProfession.setText(jsonObject.get("qualifications"))

                //Picasso
                loadImage(this, jsonObject.get("imgStr")!!, binding.viewAttachment.ivPic)

                binding.viewAttachment.btnFacebook.visibility = View.GONE
                binding.viewAttachment.btnWebsite.visibility = View.GONE
                binding.viewAttachment.btnInstagram.visibility = View.GONE
                val contacts = jsonObject.get("contact") as ArrayList<LinkedTreeMap<String,String>>
                for(contact in contacts) {
                    val type = contact.get("type")
                    val value = contact.get("value")

                    if(type.toString().equals("facebook")) {
                        binding.viewAttachment.btnFacebook.visibility = View.VISIBLE
                        binding.viewAttachment.btnFacebook.setOnClickListener {
                            openURL(this, value)
                        }
                    }
                    if(type.toString().equals("website")) {
                        binding.viewAttachment.btnWebsite.visibility = View.VISIBLE
                        binding.viewAttachment.btnWebsite.setOnClickListener {
                            openURL(this, value)
                        }
                    }
                    if(type.toString().equals("instagram")) {
                        binding.viewAttachment.btnInstagram.visibility = View.VISIBLE
                        binding.viewAttachment.btnInstagram.setOnClickListener {
                            openURL(this, value)
                        }
                    }
                }

                val bioText = jsonObject.get("bio")
                if (Build.VERSION.SDK_INT >= 24) {
                    binding.viewAttachment.tvBio.setText(Html.fromHtml(bioText, 0))
                } else {
                    binding.viewAttachment.tvBio.setText(Html.fromHtml(bioText))
                }
            }
        }
        else {
            binding.viewAttachment.root.visibility = View.GONE
        }

        val url = intent.extras!!.getString("url")
        loadImage(this, url!!, binding.ivImage)
    }


    fun GoToURL(url: String?) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}