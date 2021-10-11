package com.health.mental.mooditude.adapter

import android.R.color
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.AppUser
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.community.ApiPostComment
import com.health.mental.mooditude.utils.UiUtils.loadImage
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Jayshree Rathod on 31,August,2021
 */
class CommunityPostCommentAdapter(
    private val mContext: Context,
    private val listComments: ArrayList<ApiPostComment>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val TAG = this.javaClass.simpleName
    private val mUser:AppUser

    init {
        listComments.sortByDescending { comment -> comment.updatedAt }
        mUser = DataHolder.instance.getCurrentUser()!!
    }

    fun addAndRefresh(comment: ApiPostComment) {
        listComments.add(comment)
        listComments.sortByDescending { comment -> comment.updatedAt }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder? = null

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_feed_post_comment,
            parent,
            false
        )
        vh = CommentViewHolder(view)


        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentViewHolder) {
            holder.bind(listComments.get(position))
        }
    }

    override fun getItemCount(): Int {
        return (listComments.size)
    }


    inner class CommentViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private val ivPostedBy: ImageView
        private val tvName: TextView
        private val cardView: CardView
        private val ivImage: ImageView
        private val tvText: TextView
        private val ivStatus: ImageView
        private val tvTime: TextView
        private val tvThumbsUp: TextView
        private val tvThumbsDown: TextView
        private val yellowColor:ColorStateList
        private val primaryColor:ColorStateList

        init {
            ivPostedBy = itemView.findViewById(R.id.iv_posted_by)
            tvName = itemView.findViewById(R.id.tv_name)
            cardView = itemView.findViewById(R.id.card_image)
            ivImage = itemView.findViewById(R.id.iv_image)
            tvText = itemView.findViewById(R.id.tv_text)
            ivStatus = itemView.findViewById(R.id.iv_status)
            tvTime = itemView.findViewById(R.id.tv_time)
            tvThumbsUp = itemView.findViewById(R.id.tv_thumbsup)
            tvThumbsDown = itemView.findViewById(R.id.tv_thumbsdown)
            yellowColor = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.brand_yellow))
            primaryColor = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.primaryColor))
        }

        fun bind(comment: ApiPostComment) = with(itemView) {
            if (comment.postedBy.photo != null) {
                loadImage(mContext, comment.postedBy.photo!!, ivPostedBy)
            }

            tvName.text = comment.postedBy.name
            //loadImage(comment.)
            tvText.text = comment.text

            if (comment.isReport) {
                ivStatus.setImageResource(R.drawable.ic_error_report)
            } else {
                ivStatus.setImageDrawable(null)
            }
            val thumbsUp = comment.thumbsUp.size
            val thumbsDown = comment.thumbsDown.size
            if(thumbsUp > 0) {
                tvThumbsUp.text = thumbsUp.toString()
            }
            else {
                tvThumbsUp.text = ""
            }
            if(thumbsDown > 0) {
                tvThumbsDown.text = thumbsDown.toString()
            }
            else {
                tvThumbsDown.text = ""
            }

            tvTime.text = SimpleDateFormat("MMM dd", Locale.US).format(comment.updatedAt)

            val media1 = comment.media as ArrayList<ApiPost.Media>
            if (media1.size > 0) {
                cardView.visibility = View.VISIBLE
                loadImage(mContext, media1.get(0).url, ivImage)
            } else {
                cardView.visibility = View.GONE
            }

            tvThumbsUp.setOnClickListener {
                val userId = mUser.userId
                val removed = comment.isThumbsUp(userId)
                DBManager.instance.thumbsUpDownComment(comment.postId!!,
                comment.commentId!!,
                true, removed)
                notifyDataSetChanged()
            }

            tvThumbsDown.setOnClickListener {
                val userId = mUser.userId
                val removed = comment.isThumbsDown(userId)
                DBManager.instance.thumbsUpDownComment(comment.postId!!,
                    comment.commentId!!,
                    false, removed)
                notifyDataSetChanged()
            }

            val drawables: Array<Drawable?> = tvThumbsUp.compoundDrawablesRelative
            if(comment.thumbsUp.contains(mUser.userId)) {
                if (drawables[0] != null) {  // left drawable
                    drawables[0]!!.setTintList(yellowColor)
                }
            }
            else {
                if (drawables[0] != null) {  // left drawable
                    drawables[0]!!.setTintList(primaryColor)
                }
            }

            val drawables2: Array<Drawable?> = tvThumbsDown.compoundDrawablesRelative
            if(comment.thumbsDown.contains(mUser.userId)) {
                if (drawables2[2] != null) {  // right drawable
                    drawables2[2]!!.setTintList(yellowColor)
                }
            }
            else {
                if (drawables2[2] != null) {  // right drawable
                    drawables2[2]!!.setTintList(primaryColor)
                }
            }
        }
    }
}