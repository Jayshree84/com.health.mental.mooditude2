package com.health.mental.mooditude.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.fragment.requesttherapist.TherapistPreferenceFragment
import com.health.mental.mooditude.custom.ItemMoveCallback
import java.util.*


/**
 * Created by Jayshree Rathod on 15,July,2021
 */
class TherapistPreferenceAdapter(ctx: Context, optionsListIn: List<TherapistPreferenceFragment.TherapistPreference>,
                                 val mStartDragListener:StartDragListener) :
    RecyclerView.Adapter<TherapistPreferenceAdapter.ViewHolder>() , ItemMoveCallback.ItemTouchHelperContract {
    private val list: List<TherapistPreferenceFragment.TherapistPreference>
    private val context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_therapist_preference, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //since only one radio button is allowed to be selected,
        // this condition un-checks previous selections
        val preference = list[position]

        holder.tvTitle.text = preference.title
        holder.tvDesc.text = preference.desc
        holder.tvOrder.text = preference.order.toString()
        holder.viewDrag.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action ==
                MotionEvent.ACTION_DOWN
            ) {
                mStartDragListener.requestDrag(holder)
            }
            false
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: ViewHolder?) {
        myViewHolder!!.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.brand_yellow))
    }

    override fun onRowClear(myViewHolder: ViewHolder?) {
        myViewHolder!!.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvTitle: TextView
        var tvDesc:TextView
        var tvOrder:TextView
        var viewDrag:ImageView

        init {
            tvTitle = view.findViewById(R.id.tv_title)
            tvDesc = view.findViewById(R.id.tv_desc)
            tvOrder = view.findViewById(R.id.tv_order)
            viewDrag = view.findViewById(R.id.iv_bars)
            view.setOnClickListener {
                val position = adapterPosition
                //listener.onOptionSelected(position)
            }
        }
    }

    init {
        list = optionsListIn
        context = ctx
    }

    interface StartDragListener {
        fun requestDrag(viewHolder: RecyclerView.ViewHolder?)
    }
}