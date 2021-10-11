package com.health.mental.mooditude.custom

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Jayshree Rathod on 07,September,2021
 */
abstract class EndlessRecyclerOnScrollListener(linearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    private val previousTotal = 0 // The total number of items in the dataset after the last load
    private var loading = false // True if we are still waiting for the last set of data to load.
    private val visibleThreshold =
        1 // The minimum amount of items to have below your current scroll position before loading more.
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private var current_page = 1
    private val mLinearLayoutManager: LinearLayoutManager
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy < 0) {
            return
        }
        // check for scroll down only
        visibleItemCount = mLinearLayoutManager.childCount
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

        // to make sure only one onLoadMore is triggered
        synchronized(this) {
            //if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            /*if(!loading) {
                if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                    loading = false;
                    debugLog("Loading", "Call Load More !");
                    // End has been reached, Do something
                    current_page++
                    onLoadMore(current_page)
                    //loading = true
                }
            }*/

            //debugLog(TAG, "Load more :: recyclerView.canScrollVertically(1) : " + recyclerView.canScrollVertically(1))
            if(!recyclerView.canScrollVertically(1)) {
                // LOAD MORE
                onLoadMore(current_page)
            }
        }
    }

    fun setLoading(loading: Boolean) {
        this.loading = loading
    }

    abstract fun onLoadMore(current_page: Int)
    //abstract fun onCompleted()

    companion object {
        var TAG = EndlessRecyclerOnScrollListener::class.java.simpleName
    }

    init {
        mLinearLayoutManager = linearLayoutManager
    }
}