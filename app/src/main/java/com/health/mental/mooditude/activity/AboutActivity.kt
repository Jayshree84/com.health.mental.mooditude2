package com.health.mental.mooditude.activity

import android.content.ClipDescription
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.content.ContextCompat
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.ActivityAboutBinding
import com.health.mental.mooditude.utils.*

class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    override fun initComponents() {
        initActionBar(findViewById(R.id.toolbar))

        setPageTitle(findViewById(R.id.toolbar), "")
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brand_yellow))
        findViewById<View>(R.id.toolbar).setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.brand_yellow
            )
        )

        //set version
        binding.tvVersion.setText(packageManager.getPackageInfo(packageName, 0).versionName)

        //disclaimer desc
        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvDisclaimerDesc.setText(
                Html.fromHtml(
                    getString(R.string.disclaimer_aboout),
                    0
                )
            )
        } else {
            binding.tvDisclaimerDesc.setText(Html.fromHtml(getString(R.string.disclaimer_aboout)))
        }

        binding.btnContactEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = ClipDescription.MIMETYPE_TEXT_PLAIN
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_SUPPORT_TEAM))
            //intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject for email")
            //intent.putExtra(android.content.Intent.EXTRA_TEXT, "Description for email")
            startActivity(Intent.createChooser(intent, getString(R.string.send_email)))
        }

        binding.btnContactLink.setOnClickListener {
            openURL(this, URL_WEBSITE)
        }
        binding.btnContactFacebook.setOnClickListener {
            openURL(this, URL_FACEBOOK)
        }
        binding.btnContactInsta.setOnClickListener {
            openURL(this, URL_INSTAGRAM)
        }
        binding.tvPrivacy.setOnClickListener {
            openURL(this, URL_PRIVACY)
        }
        binding.tvTerms.setOnClickListener {
            openURL(this, URL_TERMS)
        }
    }


}