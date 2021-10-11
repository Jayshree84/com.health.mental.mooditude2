package com.health.mental.mooditude.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.custom.Circle
import com.health.mental.mooditude.data.entity.M3Assessment
import com.health.mental.mooditude.listener.OptionSelectListener
import com.health.mental.mooditude.utils.CalendarUtils


/**
 * Created by Jayshree Rathod on 15,July,2021
 */
class AssessmentListAdapter(optionsListIn: List<M3Assessment>, ctx: Context, val listener: OptionSelectListener) :
    RecyclerView.Adapter<AssessmentListAdapter.ViewHolder>() {
    private val list: List<M3Assessment>
    private val context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_assessment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //since only one radio button is allowed to be selected,
        // this condition un-checks previous selections
        val mM3Assessment = list[position]
        val allScore = mM3Assessment.allScore
        val intensity = M3AssessmentManager.getIntensityForAllScore(allScore)
        val color = M3AssessmentManager.getScoreBgColorID(intensity)

        holder.tvDate.text = CalendarUtils.formatDateForAssessment(mM3Assessment.createDate)
        holder.tvScore.text = allScore.toString()
        holder.circleScore.setColor(ContextCompat.getColor(context, color ))
        holder.tvRisk.text = context.getString(M3AssessmentManager.getRiskTextID(intensity))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvDate: TextView
        var circleScore:Circle
        var tvScore:TextView
        var tvRisk:TextView

        init {
            tvDate = view.findViewById(R.id.tv_date)
            circleScore = view.findViewById(R.id.circle_score)
            tvScore = view.findViewById(R.id.tv_score)
            tvRisk = view.findViewById(R.id.tv_risk)
            view.setOnClickListener {
                val position = adapterPosition
                listener.onOptionSelected(position)
            }
        }
    }

    init {
        list = optionsListIn
        context = ctx
    }
}