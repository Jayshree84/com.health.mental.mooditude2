package com.health.mental.mooditude.activity.ui.community

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.FirebaseStorageHelper
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.community.ApiPostComment
import com.health.mental.mooditude.data.model.community.CommunityUser
import com.health.mental.mooditude.databinding.ActivityAddNewPostBinding
import com.health.mental.mooditude.databinding.ActivityAddPostCommentBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.postCreated
import com.health.mental.mooditude.utils.FileChooser
import com.health.mental.mooditude.utils.UiUtils
import java.util.*
import kotlin.collections.ArrayList

class AddNewPostActivity : BaseActivity(), TextWatcher {

    private var mCategoryIdArray: ArrayList<String> = ArrayList()
    private var mCategoryTitleArray: ArrayList<String> = ArrayList()
    private lateinit var binding: ActivityAddNewPostBinding
    private var mPost: ApiPost? = null
    private var isInEditMode:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewPostBinding.inflate(layoutInflater)
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
            savePost()
        }

        binding.ivImage.setOnClickListener {
            binding.toolbar1.btnAddImage.callOnClick()
        }

        //check if for edit
        if (intent.extras != null) {
            val jsonText = intent.extras!!.getString("post")
            if (jsonText != null && jsonText.isNotEmpty()) {
                mPost = Gson().fromJson(jsonText, ApiPost::class.java)
            }
        }

        //Category
        //gender
        //Fetch categories
        val allCategories = DBManager.instance.getAllPostCategories()
        //create a list of categories to post
        mCategoryIdArray.clear()
        mCategoryTitleArray.clear()
        for (cat in allCategories) {
            if (cat.userCanPost) {
                mCategoryIdArray.add(cat.categoryId)
                mCategoryTitleArray.add(cat.title)
            }
        }

        setNextEnabled(false)
        binding.etMessage.addTextChangedListener(this)
        //binding.etTitle.addTextChangedListener(this)

        //check for array
        if (mCategoryIdArray.isNullOrEmpty() || mCategoryTitleArray.isNullOrEmpty()) {
            //do not add adapter
        } else {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                R.layout.select_dialog_singlechoice, mCategoryTitleArray
            )

            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnerCategory.setAdapter(adapter)
        }
        if (mPost == null) {
            val selectedCat = intent.extras!!.getString("category")!!

            if (selectedCat.trim().isNotEmpty()) {
                binding.spinnerCategory.setSelection(mCategoryIdArray.indexOf(selectedCat))
            }
        }
        else {
            isInEditMode = true
            //Check for null
            if(!mPost!!.title.isNullOrEmpty()) {
                binding.etTitle.setText(mPost!!.title)
            }
            binding.etMessage.setText(mPost!!.text)
            binding.switchAnonymous.isChecked = mPost!!.anonymousPost
            binding.spinnerCategory.setSelection(mCategoryIdArray.indexOf(mPost!!.category))
            if(mPost!!.media.size > 0) {
                val media = mPost!!.media.get(0)
                binding.ivImage.visibility = View.VISIBLE
                UiUtils.loadImage(this, media.url, binding.ivImage)
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

    private fun savePost() {
        val categoryId = mCategoryIdArray.get(binding.spinnerCategory.selectedItemPosition)
        debugLog(TAG, "Selected category :: " + categoryId)

        val postAnanomously = binding.switchAnonymous.isChecked

        /* Title is optional
        val textTitle = binding.etTitle.text.toString()
        if (textTitle.trim().isEmpty()) {
            UiUtils.showErrorToast(this, getString(R.string.please_enter_title))
            return
        }
        */

        val textPost = binding.etMessage.text.toString()
        if (textPost.trim().isEmpty()) {
            UiUtils.showErrorToast(this, getString(R.string.please_enter_detail))
            return
        }
        if(mPost == null) {
            mPost = ApiPost()
        }
        if(!binding.etTitle.text.isNullOrEmpty()) {
            mPost!!.title = binding.etTitle.text.toString()
        }
        //post.createdAt = Date(System.currentTimeMillis())
        mPost!!.postedBy = CommunityUser.createCommunityUser()
        mPost!!.category = categoryId as String
        mPost!!.text = textPost
        mPost!!.anonymousPost = postAnanomously

        //Let's upload on server
        //Let's first upload pic
        if (mPhotoUri != null && binding.ivImage.visibility == View.VISIBLE) {
            showProgressDialog(R.string.please_wait)
            FirebaseStorageHelper.instance.uploadPostImage(
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
                            mPost!!.media.clear()
                            mPost!!.media.add(media)

                            saveFeedPost()
                        }
                    }
                }, null
            )
        } else {
            saveFeedPost()
        }
    }

    private fun saveFeedPost() {
        //Save record and update UI
        DBManager.instance.addNewPost(mPost!!)
        //show toast
        if(isInEditMode) {
            UiUtils.showSuccessToast(this, getString(R.string.post_edited_successfully))
        }
        else {
            UiUtils.showSuccessToast(this, getString(R.string.post_added_successfully))
            //log event
            EventCatalog.instance.postCreated(mPost!!.postId!!, mPost!!.category,mPost!!.media.size>0)
        }
        val intent1 = Intent()
        intent1.putExtra("post", Gson().toJson(mPost!!))
        setResult(RESULT_OK, intent1)
        finish()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        enableDisableNext()
    }

    private fun enableDisableNext() {

        //Title is optional
        /*val textTitle = binding.etTitle.text.toString()
        if (textTitle.trim().isEmpty()) {
            setNextEnabled(false)
            return
        }*/

        val textPost = binding.etMessage.text.toString()
        if (textPost.trim().isEmpty()) {
            setNextEnabled(false)
            return
        }
        setNextEnabled(true)
    }
}