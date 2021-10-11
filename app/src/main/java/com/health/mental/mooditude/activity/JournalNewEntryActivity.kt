package com.health.mental.mooditude.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.FirebaseStorageHelper
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.model.journal.EntryAttachmentType
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.databinding.ActivityAddNewJournalEntryBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.utils.FileChooser
import com.health.mental.mooditude.utils.UiUtils
import java.util.*

class JournalNewEntryActivity : BaseActivity(), TextWatcher {

    private lateinit var binding: ActivityAddNewJournalEntryBinding
    private var mIsInEditMode:Boolean = false
    private var mUserEntry:Entry = Entry()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewJournalEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    override fun initComponents() {
        binding.toolbar1.tvTitle.text = getString(R.string.title_journal_new)

        if (Build.VERSION.SDK_INT >= 24) {
            binding.tvTitle2.setText(Html.fromHtml(getString(R.string.what_is_in_mind), 0))
        } else {
            binding.tvTitle2.setText(Html.fromHtml(getString(R.string.what_is_in_mind)))
        }

        //enable/disable - back n next
        binding.toolbar1.btnClose.setOnClickListener {
            finish()
        }

        binding.toolbar1.btnAddImage.setOnClickListener {
            selectImage()
        }
        binding.toolbar1.btnNext2.setOnClickListener {
            saveEntry()
        }

        binding.etMessage.addTextChangedListener(this)
        binding.ivImage.setOnClickListener {
            binding.toolbar1.btnAddImage.callOnClick()
        }

        setNextEnabled(false)

        //Check if it's in edit mode
        if (intent.extras != null && intent.extras!!.containsKey("edit_mode")) {
            mIsInEditMode = intent.extras!!.getBoolean("edit_mode", false)
            val entry = intent.extras!!.getString("entry")
            if (entry != null) {
                this.mUserEntry = Gson().fromJson(entry, Entry::class.java)
                binding.etMessage.setText(mUserEntry.post.toString())
                if (mUserEntry.imageStr != null && mUserEntry.imageStr!!.trim().isNotEmpty()) {
                    binding.ivImage.visibility = View.VISIBLE
                    UiUtils.loadImage(this, mUserEntry.imageStr!!, binding.ivImage)
                }
            }
        }
    }

    /**
     * Enables/disables NEXT button
     */
    fun setNextEnabled(flag: Boolean) {
        binding.toolbar1.btnNext2.isEnabled = flag
    }

    fun updateImageFromCamera(uri: Uri?) {
        if (uri != null) {
            updateImage(uri)
        }
    }

    fun updateImage(selectedImage: Uri) {
        //binding.ivAddImage.setImageURI(selectedImage)
        val path = FileChooser.getBitmap(this, selectedImage)
        binding.ivImage.setImageBitmap(path)
        binding.ivImage.visibility = View.VISIBLE

        mPhotoUri = selectedImage
    }

    private fun saveEntry() {
        val textPost = binding.etMessage.text.toString()
        if (textPost.trim().isEmpty()) {
            UiUtils.showErrorToast(this, getString(R.string.please_enter_text))
            return
        }
        val entry = mUserEntry
        //upload image
        //entry.imageStr =

        entry.post = textPost
        if(mIsInEditMode) {
            entry.modifiedOn = Date(System.currentTimeMillis())
        }
        else {
            entry.postedDate = Date(System.currentTimeMillis())
            entry.entryType = EntryType.journal
            entry.attachmentType = EntryAttachmentType.journal
        }
        //entry.userInfo =


        //Let's upload on server
        //Let's first upload pic
        if (mPhotoUri != null && binding.ivImage.visibility == View.VISIBLE) {
            showProgressDialog(R.string.please_wait)
            FirebaseStorageHelper.instance.uploadEntryImage(
                this, mPhotoUri,
                object : FirebaseStorageHelper.OnProgressStatusListener {
                    override fun onCompleted(argument: Any?) {

                        hideProgressDialog()
                        if (argument != null) {
                            debugLog(TAG, "Photo URL : " + argument.toString())
                            val photoUrl = argument.toString()
                            entry.imageStr = photoUrl

                            saveRecord(entry)
                        }
                    }
                }, null
            )
        } else {
            saveRecord(entry)
        }
    }

    private fun saveRecord(entry: Entry) {
        //Save record and update UI
        DBManager.instance.saveJournalEntry(entry)

        //show toast
        if(mIsInEditMode) {
            //set result
            val intent1 = Intent()
            intent1.putExtra("entry", Gson().toJson(mUserEntry))
            setResult(RESULT_OK, intent1)
        }
        else {
            //show toast
            UiUtils.showSuccessToast(this, getString(R.string.entry_added_successfully))
        }
        finish()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (s == null || s.length == 0) {
            binding.tvTotalWords.setText("")

            setNextEnabled(false)
        } else {
            // separate string around spaces
            val wordsAry =  s.split("\\s+".toRegex()).toTypedArray()
            var totalWords = 0
            for(word in wordsAry) {
                if(word.trim().isNotEmpty()) totalWords++
            }

            if(totalWords == 1) {
                binding.tvTotalWords.setText(String.format(getString(R.string.word), totalWords))
            }
            else {
                binding.tvTotalWords.setText(String.format(getString(R.string.words), totalWords))
            }
            setNextEnabled(true)
        }
    }


}