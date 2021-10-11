package com.health.mental.mooditude.fragment.journal

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.adapter.JournalEntryAdapter
import com.health.mental.mooditude.custom.EndlessRecyclerOnScrollListener
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.model.journal.EntryType
import com.health.mental.mooditude.databinding.FragmentJournalMainBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.utils.CalendarUtils
import com.health.mental.mooditude.utils.dateFromUTC
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap


/**
 * A simple [Fragment] subclass.
 * Use the [JournalMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JournalMainFragment() : BaseFragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentJournalMainBinding

    private val listEntries = ArrayList<Entry>()
    private var entriesStartDate: Date = Date(System.currentTimeMillis())
    private var canLoadMoreEntries = true
    private var lastEntryDate = Date(System.currentTimeMillis())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentJournalMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createViews()
        return root
    }

    private fun setInitialStateForEntries() {
        entriesStartDate = Date(System.currentTimeMillis())
        canLoadMoreEntries = true
        listEntries.clear()
    }

    private fun loadEntries() {
        val pageSize = 10
        if (!canLoadMoreEntries) {
            return
        }
        val newEntries = DBManager.instance.fetchMultipleJournalEntries(entriesStartDate, pageSize)
        debugLog(TAG, "New entries : " + newEntries.size)
        listEntries.addAll(newEntries)
        listEntries.sortByDescending { it.postedDate }

        if (newEntries.size > 0) {
            entriesStartDate = newEntries.get(newEntries.size - 1).postedDate
        }

        canLoadMoreEntries = (newEntries.size == pageSize)
    }


    private fun loadMoreEntries() {
        if (!canLoadMoreEntries) {
            return
        }
        loadEntries()
        prepareData()
    }

    private fun prepareData() {
        //Fetch last month entries
        val list = listEntries

        if(list.size == 0) {
            binding.listviewEntries.visibility = View.GONE
            binding.tvNoData.root.visibility = View.VISIBLE
            return
        }

        binding.listviewEntries.visibility = View.VISIBLE
        binding.tvNoData.root.visibility = View.GONE
        //first create entry view
        debugLog(TAG, "Total List size : " + listEntries.size)

        //Let's create a map date wise
        val map = LinkedHashMap<Long, ArrayList<Entry>>()
        for (item in list) {

            //Handling these entries only
            if(item.entryType == EntryType.mood || item.entryType == EntryType.journal ||
                    item.entryType == EntryType.guidedJournal) {
                val key = CalendarUtils.getStartTime(item.postedDate)
                var keyList = map.get(key)
                if (keyList != null) {
                    keyList.add(item)
                } else {
                    keyList = ArrayList<Entry>()
                    keyList.add(item)
                    map.put(key, keyList)
                }
            }
        }

        var diffDays = 0L
        var prevDays = 0L

        val mapWithMissingDays = LinkedHashMap<Int, Any>()
        var index = 0
        for (key in map.keys) {
            //For first entry
            if (prevDays == 0L && map.get(key) != null) {
                mapWithMissingDays.put(index, map.get(key)!!)
            } else {
                diffDays = CalendarUtils.getDiffInDays(Date(key), Date(prevDays))
                //debugLog(TAG, "Diff days : " + diffDays)
                if (diffDays > 1L) {
                    mapWithMissingDays.put(index, diffDays - 1)
                    index++
                }

                mapWithMissingDays.put(index, map.get(key)!!)
            }
            prevDays = key
            index++
        }

        val adapter = binding.listviewEntries.adapter
        if (adapter != null && adapter is JournalEntryAdapter) {
            adapter.refresh(mapWithMissingDays)
            //debugLog(TAG, "LIST SIZE : " + mapWithMissingDays.size)
            postAndNotifyAdapter(Handler(Looper.getMainLooper()), binding.listviewEntries, adapter,mapWithMissingDays)

        } else {
            binding.listviewEntries.adapter =
                JournalEntryAdapter(requireActivity(), mapWithMissingDays)
        }
    }

    private fun postAndNotifyAdapter(
        handler: Handler,
        recyclerView: RecyclerView,
        adapter: JournalEntryAdapter,
        mapWithMissingDays: LinkedHashMap<Int, Any>
    ) {
        handler.post(Runnable {
            debugLog(TAG, "Recyclerview : isComputingLayout " + recyclerView.isComputingLayout)
            if (!recyclerView.isComputingLayout) {
                adapter.refresh(mapWithMissingDays)
                adapter.notifyDataSetChanged()
            } else {
                postAndNotifyAdapter(handler, recyclerView, adapter, mapWithMissingDays)
            }
        })
    }

    private fun createViews() {

        setInitialStateForEntries()

        val mgr = androidx.recyclerview.widget.LinearLayoutManager(
            activity,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )
        binding.listviewEntries.layoutManager = mgr

        // enable pull up for endless loading
        val mScrollListener = object : EndlessRecyclerOnScrollListener(mgr) {
            override fun onLoadMore(current_page: Int) {
                // do something...
                debugLog(TAG, "Load More...........")
                loadMoreEntries()
                // after loading is done, please call the following method to re-enable onLoadMore
                // usually it should be called in onCompleted() method
                setLoading(false)
            }

        }
        binding.listviewEntries.addOnScrollListener(mScrollListener)

        loadEntries()
        prepareData()

        binding.fab.setOnClickListener {
            (requireActivity() as BaseActivity).addNewJournalEntry()
        }
    }

    fun deleteEntry(entry1: Entry) {
        val index = this.listEntries.indexOfFirst { entry -> entry.entryId == entry1.entryId  }
        if(index >= 0) {
            this.listEntries.removeAt(index)
            prepareData()
        }
    }

    fun updateEntry(entry1: Entry) {
        val index = this.listEntries.indexOfFirst { entry -> entry.entryId == entry1.entryId  }
        if(index >= 0) {
            this.listEntries.removeAt(index)
            this.listEntries.add(entry1)
            prepareData()
        }
    }

}