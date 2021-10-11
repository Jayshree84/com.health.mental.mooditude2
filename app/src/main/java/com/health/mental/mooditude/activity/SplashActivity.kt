package com.health.mental.mooditude.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityOptionsCompat
import com.google.firebase.auth.FirebaseAuth
import com.health.mental.mooditude.R
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.databinding.ActivitySplashBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.utils.KEY_FCM_POST_ID
import com.health.mental.mooditude.utils.SPALSH_TIME_OUT
import java.util.*


/**
 * Created by Jayshree Rathod on 02,July,2021
 */

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun initComponents() {

        //initialize components
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            kotlin.run {
                showWelcomePage()
            }
        }

        handler!!.postDelayed(runnable!!, SPALSH_TIME_OUT)

        //check for install first time
        val isAppAlreadyLaunched = SharedPreferenceManager.isAppAlreadyLaunched()
        if(isAppAlreadyLaunched == null || !isAppAlreadyLaunched) {
            //Launched first time
            debugLog(TAG, "App launched first time")
            EventCatalog.instance.installedApp()
            SharedPreferenceManager.setAppAlreadyLaunched()
        }

        //log session
        EventCatalog.instance.sessionStarted()
    }

    /**
     * Called when any activity is being destroyed
     */
    override fun onDestroy() {
        super.onDestroy()

        //Remove callbacks
        if (handler != null && runnable != null) {
            (handler as Handler).removeCallbacks(runnable!!)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_splash)
        //Data binding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent1 = intent
        val action = intent1.action
        val extras = intent1.extras
        debugLog(TAG, "Intent : action : " + action + " : " + extras)

        if (extras != null) {
            //check for postId
            if (extras.containsKey(KEY_FCM_POST_ID)) {
                val postId = extras.getString(KEY_FCM_POST_ID)
                if (postId != null && postId.trim().isNotEmpty()) {
                    //open postdetails page
                    debugLog(TAG, "PostID : " + postId)

                    //logevent
                    EventCatalog.instance.openedAppFromNotification("post", false)
                }
            }

        }
        initComponents()
    }

    protected fun isUserAlreadyLoggedIn(): Boolean {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            loginToServer(auth.currentUser!!.uid)
            return true
        }
        return false
    }

    /**
     * Show welcome page to sign-up/sign-in
     */
    private fun showWelcomePage() {
        // Pass data object in the bundle and populate details activity.

        //First check for logged user
        if (!isUserAlreadyLoggedIn()) {

            val intent1 = Intent(this@SplashActivity, WelcomeActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                (binding as ActivitySplashBinding).imgView2,
                "group"
            )

            startActivity(intent1, options.toBundle())
            //overridePendingTransition(R.anim.anim_slide_out_top, R.anim.anim_slide_in_bottom)
            finish()
        }
    }
}