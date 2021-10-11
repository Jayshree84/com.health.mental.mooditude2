package com.health.mental.mooditude.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.model.M3Question
import com.health.mental.mooditude.listener.OptionSelectListener


/**
 * Created by Jayshree Rathod on 15,July,2021
 */
class AssessmentOptionAdapter(selection:Int?, optionsListIn: List<M3Question.M3AnswerChoice>, ctx: Context, val listener: OptionSelectListener) :
    RecyclerView.Adapter<AssessmentOptionAdapter.ViewHolder>() {
    private val optionsList: List<M3Question.M3AnswerChoice>
    private val context: Context
    private var lastSelectedPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.assessment_item_option, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //since only one radio button is allowed to be selected,
        // this condition un-checks previous selections
        if(position == lastSelectedPosition) {
            //holder.tvDesc.visibility = View.VISIBLE
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        else {
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.secondaryColor))
        }
        holder.tvTitle.text = optionsList.get(position).text
    }

    override fun getItemCount(): Int {
        return optionsList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvTitle:TextView

        init {
            tvTitle = view.findViewById(R.id.tv_title)
            view.setOnClickListener {
                if(lastSelectedPosition != adapterPosition) {
                    lastSelectedPosition = adapterPosition
                    notifyDataSetChanged()

                    //call listener to update next button
                    listener.onOptionSelected(lastSelectedPosition)
                }
            }
        }
    }

    init {
        optionsList = optionsListIn
        context = ctx
        if(selection != null) {
            lastSelectedPosition = selection
        }
    }
}