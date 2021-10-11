package com.health.mental.mooditude.activity.ui.community

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.adapter.CommunityPostCommentAdapter
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.UserProfile
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.community.ApiPostComment
import com.health.mental.mooditude.databinding.FragmentPostDetailsBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.utils.UiUtils
import java.text.SimpleDateFormat
import java.util.*


class PostDetailsFragment(private var mPostDetails: ApiPost) : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentPostDetailsBinding

    companion object {
        fun newInstance(post: ApiPost): PostDetailsFragment {
            val fragment = PostDetailsFragment(post)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createViews()
        return root
    }

    private fun createViews() {

        debugLog(TAG, "Post : " + mPostDetails.toString())
        binding.listviewComments.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                activity,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
            )

        binding.listviewComments.isNestedScrollingEnabled = false

        //also fetch comments again
        DBManager.instance.getPostComments(
            mPostDetails.postId!!,
            object : FBQueryCompletedListener {
                override fun onResultReceived(result: Any?) {
                    if (result != null) {
                        val list = result as ArrayList<ApiPostComment>
                        if (list.size > 0) {

                            if (getActivity() != null && isAdded()) {
                                val adapter = CommunityPostCommentAdapter(
                                    requireActivity(), list
                                )
                                binding.listviewComments.adapter = adapter
                            }
                        }
                    }
                }
            })

        prepareUi()

        binding.ivCustomerType.visibility = View.GONE
        binding.ivCheck.visibility = View.GONE
        binding.tvCheck.visibility = View.GONE
        binding.ivStar.visibility = View.GONE
        binding.tvStar.visibility = View.GONE
        binding.ivCrown.visibility = View.GONE
        binding.tvCrown.visibility = View.GONE

        fetchPostAuthorProfile()
        updateCommentSection(mPostDetails, null)
    }

    private fun prepareUi() {

        if (!mPostDetails.anonymousPost && mPostDetails.postedBy.photo != null) {
            UiUtils.loadImage(requireContext(), mPostDetails.postedBy.photo!!, binding.ivPostedBy)
        }

        if (mPostDetails.anonymousPost) {
            binding.ivPostedBy.setImageResource(R.drawable.ic_photo_anonymous)
        }
        binding.tvType.text = mPostDetails.category

        binding.tvName.text = getString(R.string.anonymous)
        if (!mPostDetails.anonymousPost && mPostDetails.postedBy.name != null) {
            binding.tvName.text = mPostDetails.postedBy.name
        }

        //loadImage(post.)
        if(!mPostDetails.title.isNullOrEmpty()) {
            binding.tvPost.text = mPostDetails.title
        }
        else {
            binding.tvPost.text = ""
        }
        binding.tvDesc.text = mPostDetails.text

        //tvHug.text = mPostDetails.calculateReactionsCount().toString()

        binding.tvCreateDate.text =
            SimpleDateFormat("MMM dd", Locale.US).format(mPostDetails.createdAt)

        binding.cardImage.visibility = View.GONE
        binding.ivBtnPlay.visibility = View.GONE
        if (mPostDetails.media.size > 0) {
            binding.cardImage.visibility = View.VISIBLE
            UiUtils.loadImage(requireContext(), mPostDetails.media.get(0).url, binding.ivImage)

            val media = mPostDetails.media.get(0)
            if (media.type.equals("video") && media.videoUrl != null && media.videoUrl!!.trim()
                    .isNotEmpty()
            ) {
                binding.ivBtnPlay.visibility = View.VISIBLE
                binding.ivBtnPlay.setOnClickListener {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(media.videoUrl)
                        )
                    )
                    Log.i("Video", "Video Playing....")
                }
            }
        }
    }

    fun fetchPostAuthorProfile() {
        if (mPostDetails.postedBy.isCurrentUser()) {
            val userProfile = DataHolder.instance.getCurrentUserProfile()
            updateUI(userProfile!!)
            return
        }

        //Fetch user profile
        DBManager.instance.fetchUserProfile(
            mPostDetails.postedBy.userId,
            object : FBQueryCompletedListener {
                override fun onResultReceived(result: Any?) {
                    if (result != null) {
                        val userProfile = result as UserProfile
                        updateUI(userProfile)
                    }
                }
            })
    }


    fun updateUI(profile: UserProfile) {
        val userType = profile.customerType
        if (userType.equals("free")) {
            binding.ivCustomerType.visibility = View.GONE
        } else if (userType.equals("premium")) {
            binding.ivCustomerType.visibility = View.VISIBLE
            binding.ivCustomerType.setImageResource(R.drawable.ic_premiumuser)
        } else if (userType.equals("awardee")) {
            binding.ivCustomerType.visibility = View.VISIBLE
            binding.ivCustomerType.setImageResource(R.drawable.ic_useraward)
        }

        debugLog(TAG, "Profile stats : " + profile.stats.toString())
        //Badge stats -- check for counts
        if (profile.stats.checksCount > 0) {
            binding.ivCheck.visibility = View.VISIBLE
            binding.tvCheck.visibility = View.VISIBLE
            binding.tvCheck.text = profile.stats.checksCount.toString()
        } else {
            binding.ivCheck.visibility = View.GONE
            binding.tvCheck.visibility = View.GONE
        }

        if (profile.stats.starCount > 0) {
            binding.ivStar.visibility = View.VISIBLE
            binding.tvStar.visibility = View.VISIBLE
            binding.tvStar.text = profile.stats.starCount.toString()
        } else {
            binding.ivStar.visibility = View.GONE
            binding.tvStar.visibility = View.GONE
        }

        if (profile.stats.crownsCount > 0) {
            binding.ivCrown.visibility = View.VISIBLE
            binding.tvCrown.visibility = View.VISIBLE
            binding.tvCrown.text = profile.stats.crownsCount.toString()
        } else {
            binding.ivCrown.visibility = View.GONE
            binding.tvCrown.visibility = View.GONE
        }
    }

    fun refresh(post: ApiPost) {
        this.mPostDetails = post

        prepareUi()
    }

    fun updateCommentSection(post: ApiPost, data: Intent?) {
        this.mPostDetails = post
        if (mPostDetails.commentCount == 0) {
            binding.tvComments.text = getString(R.string.text_no_comments)
        } else {
            binding.tvComments.text =
                String.format(getString(R.string.text_comment_count), mPostDetails.commentCount)
        }

        if (data != null && data.extras != null && data.extras!!.containsKey("comment")) {
            val commentStr = data.extras!!.getString("comment")
            val comment = Gson().fromJson(commentStr, ApiPostComment::class.java)
            //add this comment to list and refresh
            val adapter = binding.listviewComments.adapter
            if (adapter == null) {
                val adapter1 = CommunityPostCommentAdapter(
                    requireActivity(), arrayListOf(comment)
                )
                binding.listviewComments.adapter = adapter1
            } else if (adapter is CommunityPostCommentAdapter) {
                adapter.addAndRefresh(comment)
            }
        }
    }

}
