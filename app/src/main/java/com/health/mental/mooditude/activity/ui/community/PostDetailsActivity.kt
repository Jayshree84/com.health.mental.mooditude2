package com.health.mental.mooditude.activity.ui.community

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.community.ReactionType
import com.health.mental.mooditude.databinding.ActivityPostDetailsBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.utils.*
import org.jetbrains.anko.alert


class PostDetailsActivity : BaseActivity(), PostOptionsFragment.ItemClickListener {

    private lateinit var binding: ActivityPostDetailsBinding
    private lateinit var mPost:ApiPost

    //used to check if fab menu are opened or closed
    private var closed = false
    // creating variable that handles Animations loading
    // and initializing it with animation files that we have created
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
    }

    override fun initComponents() {

        //enable/disable - back n next
        binding.toolbar.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.btnHug.setOnClickListener {
            mPost.toggleReaction()
            debugLog(TAG, "PostID ::: " + mPost.postId!!)
            DBManager.instance.addOrRemoveReaction(mPost.postId!!, ReactionType.hug, listener)
        }
        binding.toolbar.btnPin.setOnClickListener {
            mPost.toggleBookMark()
            debugLog(TAG, "PostID ::: " + mPost.postId!!)
            DBManager.instance.addOrRemoveBookmark(mPost.postId!!, listener)
        }

        val jsonText = intent.extras!!.getString("post")
        mPost = Gson().fromJson(jsonText, ApiPost::class.java)
        val postedByMe = mPost.postedBy.isCurrentUser()
        if(!postedByMe) {
            //give only "report" option
            binding.toolbar.btnMore.setImageResource(R.drawable.ic_post_report)
            binding.toolbar.btnMore.setOnClickListener {
                onItemClick(R.id.menu_report)
            }
        }
        else {
            binding.toolbar.btnMore.setOnClickListener {
                // Do Fragment menu item stuff here
                supportFragmentManager.let {
                    PostOptionsFragment.newInstance(this, postedByMe).apply {
                        show(it, tag)
                    }
                }
            }
        }

        addFragment(R.id.layout_container, PostDetailsFragment(mPost), true)

        setupFAB()
    }

    private var mIsPostEdited = false
    private val listener = object:FBQueryCompletedListener {
        override fun onResultReceived(result: Any?) {
            if(result != null && result is ApiPost) {
                mPost = result
                runOnUiThread {
                    updateFabForPost()
                    mIsPostEdited = true
                }
            }
        }
    }

    private fun setupFAB() {

        binding.fabComment.setOnClickListener {
            val intent = Intent(this, AddPostCommentActivity::class.java)
            debugLog(TAG,"post_id ::: " + mPost.postId)
            intent.putExtra("post_id", mPost.postId)
            startActivityForResult(REQUEST_ADD_POST_COMMENT, intent)
        }

        updateFabForPost()
    }

    private fun updateFabForPost() {
        val yellowColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brand_yellow))
        val primaryColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primaryColor))

        //check for bookmark and pin
        //check for fab
        if(mPost.isCurrentUserBookmarked() ) {
            binding.toolbar.btnPin.imageTintList = yellowColor
        }
        else {
            binding.toolbar.btnPin.imageTintList = primaryColor
        }

        //hug
        if(mPost.isCurrentUserReacted(ReactionType.hug) ) {
            binding.toolbar.btnHug.imageTintList = yellowColor
        }
        else {
            binding.toolbar.btnHug.imageTintList = primaryColor
        }
    }

    override fun onItemClick(itemId: Int) {
        when(itemId) {
            R.id.menu_report -> {
                //Report is clicked
                val intent = Intent(this, AddPostCommentActivity::class.java)
                intent.putExtra("post_id", mPost.postId)
                intent.putExtra("report_comment", true)
                startActivityForResult(REQUEST_ADD_POST_COMMENT_TO_REPORT, intent)
            }
            R.id.menu_edit -> {
                //Report is clicked
                val intent = Intent(this, AddNewPostActivity::class.java)
                intent.putExtra("post", Gson().toJson(mPost))
                startActivityForResult(REQUEST_EDIT_POST, intent)
            }
            R.id.menu_delete -> {
                alert(getString(R.string.are_you_sure_to_delete),
                    getString(R.string.text_delete))
                {
                    positiveButton(R.string.yes) {
                        showProgressDialog(R.string.please_wait)

                        DBManager.instance.deletePost(mPost.postId!!, object :FBQueryCompletedListener {
                            override fun onResultReceived(result: Any?) {
                                hideProgressDialog()
                                //Finish this activity
                                val intent = Intent()
                                intent.putExtra("post", Gson().toJson(mPost) )
                                setResult(RESULT_POST_DELETED, intent)
                                finish()
                            }
                        })
                    }
                    negativeButton(R.string.no) {}
                }.show()
            }
        }
    }

    fun newCommentAdded(data: Intent?) {
        mPost.updateCommentCount(mPost.commentCount+1)
        val fragment = supportFragmentManager.findFragmentByTag(PostDetailsFragment::class.java.simpleName)
        if(fragment != null) {
            (fragment as PostDetailsFragment).updateCommentSection(mPost, data)
        }
        mIsPostEdited = true
    }

    fun newCommentAddedForReport() {
        //Comment added for report
        newCommentAdded(null)
        //now report it
        showProgressDialog(R.string.please_wait)
        DBManager.instance.reportPost(mPost.postId!!, object :FBQueryCompletedListener {
            override fun onResultReceived(result: Any?) {
                if(result != null && result is ApiPost) {
                    val intent = Intent()
                    intent.putExtra("post", Gson().toJson(result) )
                    setResult(RESULT_POST_REPORTED, intent)
                }
                finish()
            }
        })
    }

    fun onEditPostCompleted(data: Intent?) {
        if(data != null && data.extras != null) {
            mPost = Gson().fromJson( data.extras!!.getString("post"), ApiPost::class.java)
            val fragment =
                supportFragmentManager.findFragmentByTag(PostDetailsFragment::class.java.simpleName)
            if (fragment != null) {
                (fragment as PostDetailsFragment).refresh(mPost)
            }
            mIsPostEdited = true
        }
    }

    override fun onBackPressed() {

        if(mIsPostEdited) {
            val intent = Intent()
            intent.putExtra("post", Gson().toJson(mPost))
            setResult(RESULT_POST_EDITED, intent)
        }
        super.onBackPressed()
    }

}