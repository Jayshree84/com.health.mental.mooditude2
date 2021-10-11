package com.health.mental.mooditude.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.JournalActivity
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.entity.JournalPrompt
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.utils.UiUtils.loadImage
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jayshree Rathod on 31,August,2021
 */
class JournalEntryAdapter(private val mContext: Context,
                          val map1: LinkedHashMap<Int, Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val TAG = this.javaClass.simpleName

    private val VIEW_WITH_DATA = 0
    private val VIEW_NO_DATA = 1
    private var map: LinkedHashMap<Int, Any>

    init {
        this.map = map1
    }

    /*init {
        map.sortBy { entry -> entry.postedDate }
    }

    fun refresh() {
        list.sortBy { entry -> entry.postedDate }
        notifyDataSetChanged()
    }*/

    override fun getItemViewType(position: Int): Int {
       val mapValue = map.get(position)
        if(mapValue != null && mapValue is ArrayList<*>) {
            return VIEW_WITH_DATA
        }
        return VIEW_NO_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder? = null
        if (viewType == VIEW_WITH_DATA) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.view_journal_entry_per_day,
                parent,
                false
            )
            vh = EntryViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.view_missing_day,
                parent,
                false
            )
            vh = MissingDayViewHolder(view)
        }

        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MissingDayViewHolder) {
            holder.bind(map.get(position) as Long)
        } else if(holder is EntryViewHolder) {
            holder.bind(map.get(position) as ArrayList<Entry>)
        }
    }

    override fun getItemCount(): Int {
        return map.size
    }

    fun refresh(map1: LinkedHashMap<Int, Any>) {
        this.map = map1
    }


    inner class MissingDayViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val tvMissingDays: TextView


        init {
            tvMissingDays = itemView.findViewById(R.id.tv_missing_day)
        }

        fun bind(days:Long) = with(itemView) {
            if(days == 1L) {
                tvMissingDays.setText(String.format(mContext.getString(R.string.missing_a_day)))
            }
            else {
                tvMissingDays.setText(String.format(mContext.getString(R.string.missing_days), days))
            }
        }
    }

    inner class EntryViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView
        private val tvDay: TextView
        private val vgEntriesContainer: ViewGroup


        init {
            tvDate = itemView.findViewById(R.id.tv_date)
            tvDay = itemView.findViewById(R.id.tv_day)
            vgEntriesContainer = itemView.findViewById(R.id.entries_container)
        }

        fun bind(list: ArrayList<Entry>) = with(itemView) {
            val date = list.get(0).postedDate
            tvDate.setText(SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date))
            tvDay.setText(SimpleDateFormat("EEEE", Locale.US).format(date))
            vgEntriesContainer.removeAllViews()
            for (item in list) {
                //debugLog(TAG, "Date : " + tvDate.text + " : " + SimpleDateFormat("dd MMM").format(item.postedDate))
                val isLastItem = (list.indexOf(item) == (list.size - 1) )
                if (item.entryType == EntryType.mood) {
                    loadMoodEntry(item, isLastItem)
                }
                else if (item.entryType == EntryType.journal) {
                    loadUnguidedEntry(item, isLastItem)
                }
                if (item.entryType == EntryType.guidedJournal) {
                    loadGuidedEntry(item, isLastItem)
                }
            }
        }

        private fun loadMoodEntry(item: Entry, isLastItem: Boolean) {
            val entryView = LayoutInflater.from(itemView.context).inflate(
                R.layout.row_entry_mood,
                itemView as ViewGroup,
                false
            )
            val ivMood = entryView.findViewById<ImageView>(R.id.iv_mood)
            ivMood.setImageDrawable(item.emotion!!.getImage(mContext))
            ivMood.isSelected = true

            val tvTime = entryView.findViewById<TextView>(R.id.tv_time)
            tvTime.setText(
                SimpleDateFormat(
                    "hh:mm aa",
                    Locale.US
                ).format(item.postedDate)
            )

            val tvImages = entryView.findViewById<TextView>(R.id.tv_images)

            tvImages.setText(item.getUserActivityImages())
            vgEntriesContainer.addView(entryView)

            entryView.setOnClickListener {
                (mContext as JournalActivity).onEntryClicked(item)
            }
            if(isLastItem) {
                //do not show divider
                entryView.findViewById<View>(R.id.view_seperator).visibility = View.INVISIBLE
            }
        }

        private fun loadUnguidedEntry(item: Entry, isLastItem: Boolean) {
            val entryView = LayoutInflater.from(itemView.context).inflate(
                R.layout.row_entry_unguided,
                itemView as ViewGroup,
                false
            )

            val tvTime = entryView.findViewById<TextView>(R.id.tv_time)
            tvTime.setText(
                SimpleDateFormat(
                    "hh:mm aa",
                    Locale.US
                ).format(item.postedDate)
            )

            val tvPost = entryView.findViewById<TextView>(R.id.tv_post)
            tvPost.setText(item.post!!.trim())

            val cardImage = entryView.findViewById<CardView>(R.id.card_image)
            val ivImage = entryView.findViewById<ImageView>(R.id.iv_image2)
            if(item.imageStr != null && item.imageStr!!.trim().isNotEmpty()) {
                cardImage.visibility = View.VISIBLE
                loadImage(mContext, item.imageStr!!, ivImage)
            }
            else {
                cardImage.visibility = View.GONE
            }

            vgEntriesContainer.addView(entryView)

            entryView.setOnClickListener {
                (mContext as JournalActivity).onEntryClicked(item)
            }
            if(isLastItem) {
                //do not show divider
                entryView.findViewById<View>(R.id.view_seperator).visibility = View.INVISIBLE
            }
        }

        private fun loadGuidedEntry(item: Entry, isLastItem: Boolean) {
            val entryView = LayoutInflater.from(itemView.context).inflate(
                R.layout.row_entry_guided,
                itemView as ViewGroup,
                false
            )

            val tvTime = entryView.findViewById<TextView>(R.id.tv_time)
            tvTime.setText(
                SimpleDateFormat(
                    "hh:mm aa",
                    Locale.US
                ).format(item.postedDate)
            )

            val journalPrompt = JournalPrompt.getPromptFromUserInfo(item.userInfo!!)
            val tvPromptTitle = entryView.findViewById<TextView>(R.id.tv_title)
            tvPromptTitle.setText(journalPrompt.title)

            /*
            //show mood
            val ivMood = entryView.findViewById<ImageView>(R.id.iv_mood)
            ivMood.setImageDrawable(item.emotion!!.getImage(mContext))
            ivMood.isSelected = true

            //add steps
            val stepContainer = entryView.findViewById<ViewGroup>(R.id.layout_inputs)
            val steps = journalPrompt.steps!!
            for(step in steps) {
                if(step != null && step.input && step.userInput != null) {
                    val stepView = LayoutInflater.from(stepContainer.context).inflate(
                        R.layout.view_entry_step,
                        stepContainer as ViewGroup,
                        false
                    )
                    val tvStepTitle = stepView.findViewById<TextView>(R.id.tv_step_title)
                    val tvStepInput = stepView.findViewById<TextView>(R.id.tv_step_input)

                    tvStepTitle.setText(step.title)
                    tvStepInput.setText(step.userInput)

                    stepContainer.addView(stepView)
                    /*stepView.setOnClickListener {
                        (mContext as JournalActivity).OnGuidedEntryStepClicked(item, step)
                    }*/
                }
            }

             */
            vgEntriesContainer.addView(entryView)

            entryView.setOnClickListener {
                (mContext as JournalActivity).onEntryClicked(item)
            }
            if(isLastItem) {
                //do not show divider
                entryView.findViewById<View>(R.id.view_seperator).visibility = View.INVISIBLE
            }
        }

    }
}