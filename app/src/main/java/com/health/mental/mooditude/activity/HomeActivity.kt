package com.health.mental.mooditude.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.ui.community.CommunityFragment
import com.health.mental.mooditude.activity.ui.community.PostDetailsActivity
import com.health.mental.mooditude.activity.ui.home.HomeFragment
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.AppUser
import com.health.mental.mooditude.data.model.community.ApiPost
import com.health.mental.mooditude.databinding.ActivityHomeBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.listener.FreshChatListener
import com.health.mental.mooditude.services.freshchat.ChatService
import com.health.mental.mooditude.utils.CalendarUtils
import com.health.mental.mooditude.utils.KEY_EXIT
import com.health.mental.mooditude.utils.KEY_FCM_POST_ID
import com.health.mental.mooditude.utils.UiUtils.loadProfileImage


class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    enum class ScreenMode {
        Home,
        Tracking,
        Community,
        Care
    }

    private lateinit var mCurrentUser: AppUser
    private var mProfileValueEventListener: ValueEventListener? = null
    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Called when required to open login page
        if (intent != null && intent.extras != null) {
            if (intent.extras!!.getBoolean(KEY_EXIT, false)) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                this.finish()
                return
            }
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_community,
                R.id.navigation_selfcare,
                R.id.navigation_tracking,
                R.id.navigation_care
            ),
            binding.mainDrawerLayout
        )

        setSupportActionBar(findViewById(R.id.main_toolbar)) //Set toolbar
        binding.mainToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primaryColor))

        navView.setupWithNavController(navController)

        navView.setOnItemReselectedListener {
            debugLog(TAG, "Do nothing")
        }

        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        ) //Setup toolbar with back button and drawer icon according to appBarConfiguration

        //binding.mainToolbar.setupWithNavController(navController, appBarConfiguration)
        //NavigationUI.setupActionBarWithNavController(this, navController, binding.mainDrawerLayout)
        //binding.mainToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_reminder, null)

        initComponents()
        //FirebaseDBManager.instance.getServerConfigurations(this)

        setupFirebaseCalls()

        binding.mainToolbar.setNavigationOnClickListener {
            onSupportNavigateUp()
        }

        mCurrentUser = DataHolder.instance.getCurrentUser()!!
        //updateUserDetails()
        addListenerForProfileDataChange()

        //make selected
        binding.mainNavigationView.setNavigationItemSelectedListener(this)

        checkForPostId()

        checkForReminder()

        //Just remove unreadcount
        showUnreadMsgCount("")

        //check for unread message count
        ChatService.instance.getUnReadCount(mListener)
        ChatService.instance.addUnreadCountListener(mListener)
    }

    private fun addListenerForProfileDataChange() {
        debugLog(TAG, "Add listener for user value event change")
        mProfileValueEventListener =
            DBManager.instance.addListenerForPrivateProfile(object : FBQueryCompletedListener {
                override fun onResultReceived(result: Any?) {
                    debugLog(TAG, "Listner result received : " + result)
                    if (result as Boolean) {
                        //update profile data
                        updateUserDetails()

                        //also update home fragment data
                        val fragment1 =
                            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
                        val fragment =
                            fragment1!!.childFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
                        if (fragment is HomeFragment) {
                            (fragment as HomeFragment).updateUserDetails()
                        }
                    }
                }
            })
    }

    private fun updateUserDetails() {
        mCurrentUser = DataHolder.instance.getCurrentUser()!!
        val ivPhoto =
            binding.mainNavigationView.getHeaderView(0).findViewById<ImageView>(R.id.iv_photo)
        if (mCurrentUser.photo.isNotEmpty()) {
            loadProfileImage(mCurrentUser.photo, ivPhoto, R.drawable.ic_profile)
        }
        ivPhoto.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
        }

        val tvName =
            binding.mainNavigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_name)
        tvName.setText(mCurrentUser.name)

        val tvEmail =
            binding.mainNavigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_email)
        tvEmail.setText(mCurrentUser.email)
    }

    private fun setupFirebaseCalls() {

        //Fetch all assessments
        DBManager.instance.fetchAllM3Assessments()

        //User is at dashboard, so add other routines for firebase calls
        DBManager.instance.addListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (mHomeEnabled) {
            //return navController.navigateUp()
            return NavigationUI.navigateUp(navController, appBarConfiguration)
        } else {
            onBackPressed()
            return true
        }
    }

    private var mHomeEnabled = true
    fun showCloseBtn(backButton: Boolean = false) {
        if (backButton) {
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_forward)
        } else {
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)
        }
        //supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        //supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(false)
        mHomeEnabled = false
        binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    fun showHomeBtn() {
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_drawer)
        mHomeEnabled = true
        binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    override fun initComponents() {
        val drawer = binding.mainDrawerLayout
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
        }

        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer,
            binding.mainToolbar,
            R.string.home_navigation_drawer_open,
            R.string.home_navigation_drawer_close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()


        //Fetch screen mode from intent extras
        updateScreen()
    }

    private fun checkForPostId() {
        //now check for postId
        //check for postId
        val extras = intent.extras
        if (extras != null && extras.containsKey(KEY_FCM_POST_ID)) {
            val postId = extras.getString(KEY_FCM_POST_ID)
            if (postId != null && postId.trim().isNotEmpty()) {
                //open postdetails page
                debugLog(TAG, "Fetching PostID : " + postId)
                DBManager.instance.getPost(postId, object : FBQueryCompletedListener {
                    override fun onResultReceived(result: Any?) {
                        if (result != null && result is ApiPost) {
                            val post = result as ApiPost
                            val intent1 = Intent(this@HomeActivity, PostDetailsActivity::class.java)
                            intent1.putExtra("post", Gson().toJson(post))
                            startActivity(intent1)
                        }
                    }

                })

            }
        }
    }

    private fun checkForReminder() {
        if (mCurrentUser.activatedReminderAtStartup) {
            CalendarUtils.setReminderForUser(this)
        } else {
            CalendarUtils.cancelAlarmForUser(this)
        }
    }

    private fun updateScreen() {
        var screenMode = ScreenMode.Home
        if (intent.extras != null) {
            screenMode = intent.extras!!.get("mode") as ScreenMode
        }

        if (screenMode == ScreenMode.Tracking) {

            //let's add argument
            /*navController.graph.findNode(R.id.navigation_tracking)!!
                .addArgument(
                    "mode", NavArgument.Builder()
                        .setDefaultValue(mode)
                        .build()
                )*/

            //Let's move to tracking first
            binding.navView.setSelectedItemId(R.id.navigation_tracking)
        } else if (screenMode == ScreenMode.Care) {
            //Let's move to care
            binding.navView.setSelectedItemId(R.id.navigation_care)
        } else if (screenMode == ScreenMode.Community) {
            //Let's move to care
            binding.navView.setSelectedItemId(R.id.navigation_community)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent1: Intent?) {
        super.onNewIntent(intent1)
        intent = intent1
        debugLog(TAG, "New Intent : " + intent.extras)
        if (intent != null && intent.extras != null) {
            if (intent.extras!!.getBoolean(KEY_EXIT, false)) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                this.finish()
                return
            }
        }
        updateScreen()
        checkForPostId()
        checkForReminder()
    }

    /*private fun clickonDashboard() {
        onNavigationItemSelected(binding.mainNavigationView.menu.findItem(R.id.menud_dashboard))
        binding.mainNavigationView.menu.findItem(R.id.menud_dashboard).isChecked = true
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Handle bottom_navigation_main view item clicks here.
        when (item.itemId) {
            R.id.menud_journal -> {
                startActivity(Intent(this, JournalActivity::class.java))
            }
            R.id.menud_settings -> {
                startActivity(Intent(this, PreferencesActivity::class.java))
            }
            R.id.menu_help -> {
                ChatService.instance.show(this)
            }
            R.id.menu_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private var animator: ObjectAnimator? = null
    fun slideDown() {
        /*
        binding.navView.let {
            if (animator == null && it.translationY == 0f) {
                animator = translationObjectY(
                    it,
                    0f,
                    it.height.toFloat() + it.marginBottom.toFloat()
                ).apply {
                    doOnEnd {
                        animator = null
                    }
                }
            }
        }
         */
    }

    fun slideUp() {
        /*
        binding.navView.let {
            if (animator == null && it.translationY == it.height.toFloat() + it.marginBottom.toFloat()) {
                animator = translationObjectY(
                    it,
                    it.height.toFloat() + it.marginBottom.toFloat(),
                    0f
                ).apply {
                    doOnEnd {
                        animator = null
                    }
                }
            }
        }
         */
    }

    fun translationObjectY(
        targetView: View?,
        startY: Float,
        endY: Float,
        duration: Long = 200L
    ): ObjectAnimator {
        return ObjectAnimator.ofFloat(targetView, "translationY", startY, endY).apply {
            this.duration = duration
            interpolator = LinearOutSlowInInterpolator()
            start()
        }
    }


/*    override fun onBackPressed() {
        super.onBackPressed()
        debugLog(TAG, " navController.graph.startDestination == navController.currentDestination?.id  : "+
                (navController.graph.startDestination == navController.currentDestination?.id) )
        if ( navController.graph.startDestination == navController.currentDestination?.id ) {
            debugLog(TAG, "Exit the application")
            exitTheApplication()
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()

        removeListenerForProfileDataChange()
        ChatService.instance.removeUnreadCountListener(mListener)
    }

    private fun removeListenerForProfileDataChange() {
        if (mProfileValueEventListener != null) {
            DBManager.instance.removeProfileDataChangeListener(
                mCurrentUser.userId,
                mProfileValueEventListener!!
            )
        }
    }

    fun newCommentAdded(data: Intent?) {
        val fragment1 =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
        val fragment =
            fragment1!!.childFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
        debugLog(TAG, "Fragment : " + fragment1 + " : " + fragment)
        if (fragment is CommunityFragment) {
            (fragment as CommunityFragment).newCommentAdded(data)
        }
    }

    fun postDetailsClosed(resultCode: Int, data: Intent?) {
        val fragment1 =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
        val fragment =
            fragment1!!.childFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
        debugLog(TAG, "Fragment : " + fragment1 + " : " + fragment)
        if (fragment is CommunityFragment) {
            (fragment as CommunityFragment).postDetailsClosed(resultCode, data)
        }
    }

    fun newPostAdded(data: Intent?) {
        val fragment1 =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
        val fragment =
            fragment1!!.childFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
        debugLog(TAG, "Fragment : " + fragment1 + " : " + fragment)
        if (fragment is CommunityFragment) {
            (fragment as CommunityFragment).newPostAdded(data)
        }
    }

    private val mListener = object : FreshChatListener {
        override fun getUnreadCountReceived(unreadCount: Int) {
            if (unreadCount == 0) {
                showUnreadMsgCount("")
            } else {
                showUnreadMsgCount(unreadCount.toString())
            }
        }
    }

    private fun showUnreadMsgCount(unreadCount: String) {
        val tvUnreadCount =
            binding.mainNavigationView.menu.findItem(R.id.menu_help).actionView as TextView
        tvUnreadCount.text = unreadCount
        if (unreadCount.trim().isEmpty()) {
            tvUnreadCount.visibility = View.INVISIBLE
        } else {
            tvUnreadCount.visibility = View.VISIBLE
        }
    }

}