package com.health.mental.mooditude.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.activity.ui.community.AddPostCommentActivity
import com.health.mental.mooditude.activity.ui.community.CommunityFragment
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.PostCategory
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.community.ReactionType
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.utils.REQUEST_ADD_POST_COMMENT
import com.health.mental.mooditude.utils.UiUtils
import com.health.mental.mooditude.utils.UiUtils.loadImage
import org.jetbrains.anko.runOnUiThread
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jayshree Rathod on 31,August,2021
 */
class CommunityPostAdapter(
    private val mContext: Context,
    private val listPostCategories: ArrayList<PostCategory>?,
    private var listPost: ArrayList<ApiPost>,
    private val mParentFragment: CommunityFragment,
    private val mCurrFragmentName: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val TAG = this.javaClass.simpleName

    private val VIEW_CATEGORY = 0
    private val VIEW_POST = 1

    init {
        if (listPostCategories != null) listPostCategories.sortBy { cat -> cat.position }
        //listPost.sortByDescending { post -> post.updatedAt }
    }

    fun refresh(list: ArrayList<ApiPost>) {
        listPost = list
        //listPost.sortByDescending { entry -> entry.updatedAt }
        //notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (listPostCategories != null && listPostCategories.size > 0 && position == 0) {
            return VIEW_CATEGORY
        }
        return VIEW_POST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder? = null
        if (viewType == VIEW_CATEGORY) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.row_post_category,
                parent,
                false
            )
            vh = CategoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.row_feed_post,
                parent,
                false
            )
            vh = FeedPostViewHolder(view)
        }

        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CategoryViewHolder) {
            holder.bind(listPostCategories!!)
        } else if (holder is FeedPostViewHolder) {
            if (getItemViewType(0) == VIEW_CATEGORY) {
                holder.bind(listPost.get(position - 1), position)
            } else {
                holder.bind(listPost.get(position), position)
            }
        }
    }

    override fun getItemCount(): Int {
        if (listPostCategories != null && listPostCategories.size > 0) {
            return (listPost.size + 1)
        } else {
            return listPost.size
        }
    }

    /*fun itemChanged(index: Int, post: ApiPost) {
        //this.listPost.removeAt(index)
        this.listPost.add(index, post)
        this.listPost.removeAt(index+1)
    }*/


    inner class FeedPostViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private val ivPostedBy: ImageView
        private val tvType: TextView
        private val tvName: TextView
        private val ivImage: ImageView
        private val cardImage: CardView
        private val ivVideoIndicator: ImageView
        private val tvPost: TextView
        private val tvDesc: TextView
        private val tvTime: TextView
        private val tvComments: TextView
        private val tvHug: TextView

        init {
            ivPostedBy = itemView.findViewById(R.id.iv_posted_by)
            tvType = itemView.findViewById(R.id.tv_type)
            tvName = itemView.findViewById(R.id.tv_name)
            ivImage = itemView.findViewById(R.id.iv_image)
            cardImage = itemView.findViewById(R.id.card_image)
            ivVideoIndicator = itemView.findViewById(R.id.iv_video_indicator)
            tvPost = itemView.findViewById(R.id.tv_post)
            tvDesc = itemView.findViewById(R.id.tv_desc)
            tvTime = itemView.findViewById(R.id.tv_time)
            tvComments = itemView.findViewById(R.id.tv_comments)
            tvHug = itemView.findViewById(R.id.tv_hug)
        }

        fun bind(post: ApiPost, position: Int) = with(itemView) {
            if (!post.anonymousPost && post.postedBy.photo != null) {
                loadImage(mContext, post.postedBy.photo!!, ivPostedBy)
            }

            if(post.anonymousPost) {
                ivPostedBy.setImageResource(R.drawable.ic_photo_anonymous)
            }

            tvName.text = mContext.getString(R.string.anonymous)
            if (!post.anonymousPost && post.postedBy.name != null) {
                tvName.text = post.postedBy.name
            }

            tvType.text = post.category

            debugLog(TAG, "POST :: " + post.title + " : " + post.postId)

            //loadImage(post.)
            if(!post.title.isNullOrEmpty()) {
                tvPost.text = post.title!!.trim()
            }
            else {
                tvPost.text = ""
            }
            tvDesc.text = post.text.trim()

            tvComments.text = post.commentCount.toString()
            tvHug.text = post.calculateReactionsCount().toString()

            tvTime.text = String.format(
                mContext.getString(R.string.last_activity),
                SimpleDateFormat("MMM dd", Locale.US).format(post.updatedAt)
            )

            cardImage.visibility = View.GONE
            ivVideoIndicator.visibility = View.GONE
            debugLog(TAG, "post.media : " + post.media.toString())
            if (post.media.size > 0) {
                cardImage.visibility = View.VISIBLE
                loadImage(mContext, post.media.get(0).url, ivImage)
                if (post.media.get(0).type.equals("video", true)) {
                    ivVideoIndicator.visibility = View.VISIBLE
                }
            }

            itemView.setOnClickListener {
                mParentFragment.onPostItemClicked(post, mCurrFragmentName)
            }
            tvHug.setOnClickListener {
                post.toggleReaction()
                DBManager.instance.addOrRemoveReaction(post.postId!!, ReactionType.hug, object : FBQueryCompletedListener {
                    override fun onResultReceived(result: Any?) {
                        if(result != null && result is ApiPost) {
                            mContext.runOnUiThread {
                                mParentFragment.toggleReaction(result)
                            }
                        }
                    }
                })
            }
            tvComments.setOnClickListener {
                val intent = Intent(mContext, AddPostCommentActivity::class.java)
                intent.putExtra("post_id", post.postId)
                (mContext as BaseActivity).startActivityForResult(REQUEST_ADD_POST_COMMENT, intent)
            }
        }
    }

    inner class CategoryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val llContainer: LinearLayout

        init {
            llContainer = itemView.findViewById(R.id.category_container)
        }

        fun bind(list: ArrayList<PostCategory>) = with(itemView) {
            llContainer.removeAllViews()
            for (item in list) {
                val view = LayoutInflater.from(llContainer.context).inflate(
                    R.layout.view_post_category,
                    llContainer,
                    false
                )

                val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                val tvDesc = view.findViewById<TextView>(R.id.tv_desc)
                val ivImage = view.findViewById<ImageView>(R.id.iv_image)
                val ivPremium = view.findViewById<ImageView>(R.id.iv_premium)

                tvTitle.setText(item.title.trim())
                tvDesc.setText(item.description)
                loadImage(mContext, item.imgStr!!, ivImage)

                if (item.isPremium) {
                    ivPremium.visibility = View.GONE
                } else {
                    ivPremium.visibility = View.GONE
                }

                llContainer.addView(view)
                view.setOnClickListener {
                    mParentFragment.onPostCatClicked(item)
                }
            }

        }


    }
}