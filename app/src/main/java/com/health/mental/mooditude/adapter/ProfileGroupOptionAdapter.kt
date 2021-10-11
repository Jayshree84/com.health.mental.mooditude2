package com.health.mental.mooditude.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.listener.GroupOptionSelectListener
import com.health.mental.mooditude.model.ProfileOption
import com.health.mental.mooditude.utils.AnimationUtils


/**
 * Created by Jayshree Rathod on 15,July,2021
 */
class ProfileGroupOptionAdapter(optionsListIn: List<ProfileOption>, ctx: Context, val listener: GroupOptionSelectListener) :
    RecyclerView.Adapter<ProfileGroupOptionAdapter.ViewHolder>() {
    private val optionsList: List<ProfileOption>
    private val context: Context
    //private var lastSelectedPosition = -1
    private val listSelections = arrayListOf<Int>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //since only one radio button is allowed to be selected,
        // this condition un-checks previous selections
        holder.selectionState.isChecked = listSelections.contains(position)
        if(holder.selectionState.isChecked) {
            //holder.tvDesc.visibility = View.VISIBLE
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
            //Only if description is not empty
            if(holder.tvDesc.text.isNotEmpty()) {
                AnimationUtils.onFlipAniPerform(holder.itemView, holder.tvDesc, true)
            }
        }
        else {
            //holder.tvDesc.visibility = View.GONE
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
            AnimationUtils.onFlipAniPerform(holder.itemView, holder.tvDesc, false)
        }
        holder.tvTitle.text = optionsList.get(position).title
        holder.tvDesc.text = optionsList.get(position).desc
    }

    override fun getItemCount(): Int {
        return optionsList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var selectionState: RadioButton
        var tvTitle:TextView
        var tvDesc:TextView

        init {
            tvTitle = view.findViewById(R.id.tv_title)
            tvDesc = view.findViewById(R.id.tv_desc)
            selectionState = view.findViewById<View>(R.id.rb_choice) as RadioButton
            view.setOnClickListener {
                val position = adapterPosition
                if(listSelections.contains(position)) {
                    //already checked
                    listSelections.remove(position)
                }
                else {
                    listSelections.add(position)
                }
                notifyDataSetChanged()
                listener.onGroupOptionsSelected(listSelections)
            }
        }
    }

    init {
        optionsList = optionsListIn
        context = ctx
    }
}