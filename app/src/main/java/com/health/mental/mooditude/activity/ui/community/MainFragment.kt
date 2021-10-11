package com.health.mental.mooditude.activity.ui.community

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.BaseActivity
import com.health.mental.mooditude.activity.HomeActivity
import com.health.mental.mooditude.adapter.CommunityPostAdapter
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.custom.EndlessRecyclerOnScrollListener
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.entity.PostCategory
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.data.model.community.ApiPostComment
import com.health.mental.mooditude.data.model.community.SortingType
import com.health.mental.mooditude.databinding.FragmentCommunityMainBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.BaseFragment
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.filteredPosts
import com.health.mental.mooditude.services.instrumentation.sortedPosts


class MainFragment() : BaseFragment(), OptionsFragment.ItemClickListener {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!! as FragmentCommunityMainBinding

    //token used to refresh data
    private var queryFirstToken: Any? = null
    private var queryLastToken: Any? = null
    private var allCategories = ArrayList<PostCategory>()
    private val allPosts = ArrayList<ApiPost>()
    private var posts = ArrayList<ApiPost>()
    private val pinnedPosts = ArrayList<ApiPost>()

    private lateinit var selectedCategory: PostCategory
    private var selectedSort: SortingType = SortingType.recentlyUpdated

    private var isFilterSelected = false
    private var selectedMenuId: Int = 0

    //has more data
    private var hasMoreData = true

    companion object {
        fun newInstance(menuId: Int, category: PostCategory? = null): MainFragment {
            val gson = Gson()
            val args = Bundle()
            args.putBoolean("filter_selected", true)
            if (category == null) {
                args.putInt("menu_id", menuId)
            } else {
                args.putInt("menu_id", -1)
                args.putString("category", Gson().toJson(category))
            }
            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            isFilterSelected = requireArguments().getBoolean("filter_selected")
            selectedMenuId = requireArguments().getInt("menu_id")
            if (selectedMenuId == -1) {
                selectedCategory = Gson().fromJson(
                    requireArguments().getString("category"),
                    PostCategory::class.java
                )
            }
            debugLog(TAG, "isFilterSelected : " + isFilterSelected + " : " + selectedMenuId)
        }
        if (!isFilterSelected) {
            setHasOptionsMenu(true)
        } else {
            (requireActivity() as HomeActivity).showCloseBtn(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.community_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_filter -> {
                // Do Fragment menu item stuff here
                childFragmentManager.let {
                    OptionsFragment.newInstance(this).apply {
                        show(it, tag)
                    }
                }
                return true
            }
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        createViews()
        return root
    }

    private fun createViews() {

        val mgr = androidx.recyclerview.widget.LinearLayoutManager(
            activity,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )
        binding.listviewEntries.layoutManager = mgr


        // enable pull down to refresh
        binding.swipeRefreshLayout.setOnRefreshListener(OnRefreshListener {
            // do something

            if (canRefresh()) {
                refreshData()
            }
            // after refresh is done, remember to call the following code
            if (binding.swipeRefreshLayout.isRefreshing()) {
                binding.swipeRefreshLayout.setRefreshing(false) // This hides the spinner
            }
        })

        // enable pull up for endless loading
        // enable pull up for endless loading
        val mScrollListener = object : EndlessRecyclerOnScrollListener(mgr) {
            override fun onLoadMore(current_page: Int) {
                // do something...
                debugLog(TAG, "Load More...........")
                fetchData()
                // after loading is done, please call the following method to re-enable onLoadMore
                // usually it should be called in onCompleted() method
                setLoading(false)
            }

        }
        binding.listviewEntries.addOnScrollListener(mScrollListener)

        if (!isFilterSelected) {
            allCategories = DBManager.instance.getAllPostCategories()

            //set default data
            selectedCategory = PostCategory.defaultCategory("")
            selectedSort = SortingType.recentlyUpdated

            fetchData()
            //addScrollListener(binding.listviewEntries)
        } else {
            onFilteringSelected(selectedMenuId)
            //set title
            (requireActivity() as BaseActivity).setPageTitle(
                requireActivity().findViewById(R.id.main_toolbar),
                this.getCategoryTitle()
            )
        }
    }

    private fun canRefresh(): Boolean {
        return !(selectedCategory.categoryId == "myBookmarks" || selectedCategory.categoryId == "myPosts")
    }

    private fun fetchData() {
        if (!hasMoreData) {
            debugLog(TAG, "No more data, so do not fetch")
            return
        }
        DBManager.instance.getFeedPostsAfter(queryLastToken, selectedCategory.categoryId,
            selectedSort, object : FBQueryCompletedListener {
                override fun onResultReceived(result: Any?) {

                    if (activity != null && isAdded) {
                        requireActivity().runOnUiThread {
                            if (result != null && result is HashMap<*, *>) {
                                val map = result as HashMap<*, *>
                                val firstToken = map.get("first")
                                if (firstToken != null) {
                                    queryFirstToken = firstToken
                                }
                                queryLastToken = map.get("last")
                                hasMoreData = (queryLastToken != null)
                                debugLog(
                                    TAG,
                                    "hasMoreData : " + hasMoreData + " : " + queryLastToken
                                )

                                val list = map.get("list") as ArrayList<ApiPost>
                                processFetchedPosts(list)
                                filterData()
                                sortData()
                                prepareData()
                            }
                        }
                    }
                }

            })
    }

    private fun refreshData() {

        if (queryFirstToken == null) {
            fetchData()
            return
        }

        DBManager.instance.getFeedPostsBefore(queryFirstToken, selectedCategory.categoryId,
            selectedSort, object : FBQueryCompletedListener {
                override fun onResultReceived(result: Any?) {

                    if (activity != null && isAdded) {
                        requireActivity().runOnUiThread {
                            if (result != null && result is HashMap<*, *>) {
                                val map = result as HashMap<*, *>
                                val firstToken = map.get("first")
                                if (firstToken != null) {
                                    queryFirstToken = firstToken
                                }
                                //queryLastToken = map.get("last")
                                hasMoreData = (queryLastToken != null)

                                val list = map.get("list") as ArrayList<ApiPost>
                                processFetchedPosts(list)
                                filterData()
                                sortData()
                                prepareData()
                            }
                        }
                    }
                }

            })
    }

    private fun processFetchedPosts(posts: ArrayList<ApiPost>) {
        if (posts.size == 0) return

        posts.forEach { post ->
            val index = allPosts.indexOfFirst { it.postId == post.postId }
            if (index != -1) {
                allPosts.removeAt(index)
            }
        }

        //append the list
        this.allPosts.addAll(posts)
    }

    private fun filterData() {
        posts.clear()
        pinnedPosts.clear()

        val userId = DataHolder.instance.getCurrentUserId()

        when (selectedCategory.categoryId) {
            "myBookmarks" -> {
                this.posts.addAll(allPosts.filter { p ->
                    (p.bookmarks.contains(userId) && !p.pinned && p.status != ApiPost.PostStatus.blocked)
                })
            }
            "myPosts" -> {
                this.posts.addAll(allPosts.filter { p ->
                    (p.postedBy.userId == userId && !p.pinned)
                })
            }
            "reportedPosts" -> {
                this.posts.addAll(allPosts.filter { p ->
                    (p.status == ApiPost.PostStatus.reported)
                })
            }
            "none" -> {
                this.posts.addAll(allPosts.filter { p ->
                    (!p.pinned && p.status != ApiPost.PostStatus.blocked)
                })

                val allPinnedPosts = ArrayList<ApiPost>()
                allPinnedPosts.addAll(allPosts.filter { p ->
                    p.pinned && p.status != ApiPost.PostStatus.blocked && p.showOnMain
                })

                val latestPinnedPost = allPinnedPosts.maxByOrNull { post -> post.createdAt }

                if (latestPinnedPost != null) {
                    pinnedPosts.add(latestPinnedPost)
                }
            }
            else -> {
                this.posts.addAll(allPosts.filter { p ->
                    (p.category.equals(selectedCategory.categoryId) && !p.pinned &&
                            p.status != ApiPost.PostStatus.blocked)
                })

                val allPinnedPosts = ArrayList<ApiPost>()
                allPinnedPosts.addAll(allPosts.filter { p ->
                    p.pinned && p.category.equals(selectedCategory.categoryId) &&
                            p.status != ApiPost.PostStatus.blocked
                })

                val latestPinnedPost = allPinnedPosts.maxByOrNull { post -> post.createdAt }

                if (latestPinnedPost != null) {
                    pinnedPosts.add(latestPinnedPost)
                }
            }
        }
    }

    private fun sortData() {
        when (selectedSort) {
            SortingType.recentlyUpdated -> {
                posts.sortByDescending { p -> p.updatedAt }
            }
            SortingType.new -> {
                posts.sortByDescending { p -> p.createdAt }
            }
            else -> {
                posts.sortByDescending { p -> p.activityCount }
            }
        }
    }

    private fun prepareData() {
        var adapter = binding.listviewEntries.adapter
        if (adapter != null && adapter is CommunityPostAdapter) {
            adapter.refresh(posts)
            debugLog(TAG, "LIST SIZE : " + posts.size)
            adapter.notifyDataSetChanged()
        } else {
            adapter = CommunityPostAdapter(
                requireActivity(), allCategories, posts,
                parentFragment as CommunityFragment,
                MainFragment::class.java.simpleName
            )
            binding.listviewEntries.adapter = adapter
        }
        //binding.listviewEntries.smoothScrollToPosition(0)
        //binding.progressBar.visibility = View.GONE
    }

    private fun onFilteringSelected(itemId: Int) {

        //check for post category
        if (itemId == -1 && !selectedCategory.categoryId.equals("none")) {
            selectedSort = SortingType.recentlyUpdated
            queryFirstToken = null
            queryLastToken = null
//            binding.progressBar.visibility = View.VISIBLE
            fetchData()
            return
        }

        when (itemId) {
            R.id.menu_fresh -> {
                selectedSort = SortingType.recentlyUpdated
                selectedCategory = PostCategory.defaultCategory(getString(R.string.text_fresh))
                queryFirstToken = null
                queryLastToken = null
//                binding.progressBar.visibility = View.VISIBLE
                fetchData()
            }
            R.id.menu_popular -> {
                selectedSort = SortingType.popular
                selectedCategory = PostCategory.defaultCategory(getString(R.string.text_popular))
                queryFirstToken = null
                queryLastToken = null
//                binding.progressBar.visibility = View.VISIBLE
                fetchData()
            }
            R.id.menu_active -> {
                //selectedSort = SortingType.
                //selectedCategory = PostCategory.defaultCategory()
                queryFirstToken = null
                queryLastToken = null
//                binding.progressBar.visibility = View.VISIBLE
                fetchData()
            }
            R.id.menu_your_posts -> {
                selectedSort = SortingType.new
                selectedCategory = PostCategory.myPostsCategory()
                hasMoreData = false
//                binding.progressBar.visibility = View.VISIBLE
                fetchMyPosts()
            }
            R.id.menu_your_bookmark -> {
                selectedSort = SortingType.recentlyUpdated
                selectedCategory = PostCategory.myBookmarksCategory()
                hasMoreData = false
//                binding.progressBar.visibility = View.VISIBLE
                fetchBookmarks()
            }
            R.id.menu_reported -> {
                selectedSort = SortingType.recentlyUpdated
                selectedCategory = PostCategory.reportedPostsCategory()
                hasMoreData = false
//                binding.progressBar.visibility = View.VISIBLE
                fetchReportedPosts()
            }
        }

        //Log event
        EventCatalog.instance.filteredPosts(selectedCategory.categoryId)
        EventCatalog.instance.sortedPosts(selectedSort.getValue())
    }

    override fun onItemClick(itemId: Int) {
        (parentFragment as CommunityFragment).onMenuClicked(itemId)
    }

    private fun fetchMyPosts() {

        DBManager.instance.getMyPosts(selectedSort, object : FBQueryCompletedListener {
            override fun onResultReceived(result: Any?) {
                if (activity != null && isAdded) {
                    requireActivity().runOnUiThread {
                        //binding.progressBar.visibility = View.GONE
                        if (result != null && result is HashMap<*, *>) {
                            val map = result
                            val list: ArrayList<ApiPost> = map.get("list") as ArrayList<ApiPost>
                            queryFirstToken = null
                            queryLastToken = null
                            hasMoreData = false

                            processFetchedPosts(list)
                            filterData()
                            sortData()
                            prepareData()
                        }
                    }
                }
            }
        })
    }

    private fun fetchBookmarks() {

        DBManager.instance.getBookmarks(selectedSort, object : FBQueryCompletedListener {
            override fun onResultReceived(result: Any?) {
                if (activity != null && isAdded) {
                    requireActivity().runOnUiThread {
//                        binding.progressBar.visibility = View.GONE
                        if (result != null && result is HashMap<*, *>) {
                            val map = result
                            val list: ArrayList<ApiPost> = map.get("list") as ArrayList<ApiPost>

                            queryFirstToken = null
                            queryLastToken = null
                            hasMoreData = false

                            processFetchedPosts(list)
                            filterData()
                            sortData()
                            prepareData()
                        }
                    }
                }

            }
        })

    }

    private fun fetchReportedPosts() {

        DBManager.instance.getReportedPosts(selectedSort, object : FBQueryCompletedListener {
            override fun onResultReceived(result: Any?) {
                if (activity != null && isAdded) {
                    requireActivity().runOnUiThread {
//                        binding.progressBar.visibility = View.GONE
                        if (result != null && result is HashMap<*, *>) {
                            val map = result
                            val list: ArrayList<ApiPost> = map.get("list") as ArrayList<ApiPost>

                            queryFirstToken = null
                            queryLastToken = null
                            hasMoreData = false

                            processFetchedPosts(list)
                            filterData()
                            sortData()
                            prepareData()
                        }
                    }
                }
            }
        })
    }

    fun isFilteringSelected() = isFilterSelected
    fun getCategoryTitle() = selectedCategory.title
    fun getCategoryId() = selectedCategory.categoryId
    fun newCommentAdded(data: Intent?) {
        if (data != null && data.extras != null && data.extras!!.containsKey("comment")) {
            val commentStr = data.extras!!.getString("comment")
            val comment = Gson().fromJson(commentStr, ApiPostComment::class.java)
            updateCommentCount(comment)
        }
    }

    private fun updateCommentCount(comment: ApiPostComment) {

        val index = posts.indexOfFirst { it.postId == comment.postId }
        if (index >= 0) {
            val post = posts.get(index)
            post.updateCommentCount(post.commentCount + 1)
            posts.removeAt(index)
            posts.add(index, post)
            sortData()
            prepareData()
        }
    }

    fun updateReaction(post1: ApiPost) {

        val index = posts.indexOfFirst { it.postId == post1.postId }
        if (index >= 0) {

            //post.toggleReaction()
            posts.removeAt(index)
            posts.add(index, post1)
            sortData()
            prepareData()
        }
    }

    fun postDeleted(post1: ApiPost) {

        val index = posts.indexOfFirst { it.postId == post1.postId }
        if (index >= 0) {

            //post.toggleReaction()
            posts.removeAt(index)
            sortData()
            prepareData()
        }
    }

    fun postAdded(data: Intent?) {

        if (data != null && data.extras != null && data.extras!!.containsKey("post")) {
            val postStr = data.extras!!.getString("post")
            val post = Gson().fromJson(postStr, ApiPost::class.java)

            val index = posts.indexOfFirst { it.postId == post.postId }
            //If post not found
            if (index < 0) {
                //Do not add directly - filter and add post
                //posts.add(post1)
                val isAdded = filterAndAddNewlyCreatedPost(post)
                debugLog(TAG, "Is Post Added : " + isAdded)
                if (isAdded) {
                    sortData()
                    prepareData()
                }
            }
        }
    }

    private fun filterAndAddNewlyCreatedPost(post: ApiPost): Boolean {

        var isAdded = false
        val userId = DataHolder.instance.getCurrentUserId()

        when (selectedCategory.categoryId) {
            "myBookmarks" -> {
                if (post.bookmarks.contains(userId) &&
                    !post.pinned && post.status != ApiPost.PostStatus.blocked
                ) {
                    this.posts.add(post)
                    isAdded = true
                }
            }
            "myPosts" -> {
                if (post.postedBy.userId == userId && !post.pinned) {
                    this.posts.add(post)
                    isAdded = true
                }
            }
            "reportedPosts" -> {
                if (post.status == ApiPost.PostStatus.reported) {
                    this.posts.add(post)
                    isAdded = true
                }
            }
            "none" -> {
                if (!post.pinned && post.status != ApiPost.PostStatus.blocked) {
                    this.posts.add(post)
                    isAdded = true
                }

                /*
                val allPinnedPosts = ArrayList<ApiPost>()
                allPinnedPosts.addAll(allPosts.filter { p ->
                    p.pinned && p.status != ApiPost.PostStatus.blocked && p.showOnMain
                })

                val latestPinnedPost = allPinnedPosts.maxByOrNull { post -> post.createdAt }

                if (latestPinnedPost != null) {
                    pinnedPosts.add(latestPinnedPost)
                }*/
            }
            else -> {
                if (post.category.equals(selectedCategory.categoryId) && !post.pinned &&
                    post.status != ApiPost.PostStatus.blocked
                ) {
                    this.posts.add(post)
                    isAdded = true
                }

                /*
                val allPinnedPosts = ArrayList<ApiPost>()
                allPinnedPosts.addAll(allPosts.filter { p ->
                    p.pinned && p.category.equals(selectedCategory.categoryId) &&
                            p.status != ApiPost.PostStatus.blocked
                })

                val latestPinnedPost = allPinnedPosts.maxByOrNull { post -> post.createdAt }

                if (latestPinnedPost != null) {
                    pinnedPosts.add(latestPinnedPost)
                }*/
            }
        }
        return isAdded
    }


    fun postEdited(post1: ApiPost) {

        val index = posts.indexOfFirst { it.postId == post1.postId }
        if (index >= 0) {

            //post.toggleReaction()
            posts.removeAt(index)
            posts.add(index, post1)
            sortData()
            prepareData()
        }
    }

}
