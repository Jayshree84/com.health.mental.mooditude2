package com.health.mental.mooditude.custom


/**
 * Created by Jayshree Rathod on 10,August,2021
 */

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.adapter.TherapistPreferenceAdapter


class ItemMoveCallback(private val mAdapter: ItemTouchHelperContract) :
    ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {}
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is TherapistPreferenceAdapter.ViewHolder) {
                val myViewHolder: TherapistPreferenceAdapter.ViewHolder? =
                    viewHolder as TherapistPreferenceAdapter.ViewHolder?
                mAdapter.onRowSelected(myViewHolder)
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is TherapistPreferenceAdapter.ViewHolder) {
            val myViewHolder: TherapistPreferenceAdapter.ViewHolder =
                viewHolder as TherapistPreferenceAdapter.ViewHolder
            mAdapter.onRowClear(myViewHolder)
        }
    }

    interface ItemTouchHelperContract {
        fun onRowMoved(fromPosition: Int, toPosition: Int)
        fun onRowSelected(myViewHolder: TherapistPreferenceAdapter.ViewHolder?)
        fun onRowClear(myViewHolder: TherapistPreferenceAdapter.ViewHolder?)
    }
}

