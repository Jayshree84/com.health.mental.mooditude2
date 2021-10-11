package com.health.mental.mooditude.activity.ui.community

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.FirebaseStorageHelper
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.community.ApiPostComment
import com.health.mental.mooditude.data.model.community.CommunityUser
import com.health.mental.mooditude.databinding.ActivityAddPostCommentBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.utils.FileChooser
import com.health.mental.mooditude.utils.UiUtils
import java.util.*
import kotlin.collections.ArrayList

class AddPostCommentActivity : BaseActivity(), TextWatcher {

    private lateinit var binding: ActivityAddPostCommentBinding
    private var reportComment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPostCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    override fun initComponents() {
        binding.toolbar1.tvTitle.text = ""

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

        binding.ivImage.setOnClickListener {
            binding.toolbar1.btnAddImage.callOnClick()
        }

        //check if comment is for report
        if(intent.extras != null) {
            reportComment = intent.extras!!.getBoolean("report_comment", false)
            if(reportComment) {
                binding.tvTitle2.setText(R.string.why_post_removed)
                binding.toolbar1.btnAddImage.visibility = View.INVISIBLE
            }
        }
        binding.etMessage.addTextChangedListener(this)
        setNextEnabled(false)
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
        val comment = ApiPostComment()
        //check for report comment
        comment.isReport = reportComment
        //upload image
        //entry.imageStr =

        val currPostId = intent.extras!!.getString("post_id")

        comment.text = textPost
        comment.createdAt = Date(System.currentTimeMillis())
        comment.postId = currPostId
        comment.postedBy = CommunityUser.createCommunityUser()

        //Let's upload on server
        //Let's first upload pic
        if (mPhotoUri != null && binding.ivImage.visibility == View.VISIBLE) {
            showProgressDialog(R.string.please_wait)
            FirebaseStorageHelper.instance.uploadCommentImage(
                this, mPhotoUri,
                object : FirebaseStorageHelper.OnProgressStatusListener {
                    override fun onCompleted(argument: Any?) {

                        hideProgressDialog()
                        if (argument != null) {
                            debugLog(TAG, "Photo URL : " + argument.toString())
                            val photoUrl = argument.toString()
                            val media = ApiPost.Media()
                            media.type = "image"
                            media.url = photoUrl
                            (comment.media as ArrayList<ApiPost.Media>).add(media)

                            saveComment(comment)
                        }
                    }
                }, null
            )
        } else {
            saveComment(comment)
        }
    }

    private fun saveComment(comment: ApiPostComment) {
        //Save record and update UI
        DBManager.instance.addNewPostComment(comment)
        //show toast
        UiUtils.showSuccessToast(this, getString(R.string.comment_added_successfully))
        val data = Intent()
        data.putExtra("comment", Gson().toJson(comment))
        setResult(RESULT_OK, data)
        finish()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s == null || s.length == 0) {
            setNextEnabled(false)
        }
        else {
            setNextEnabled(true)
        }
    }

}