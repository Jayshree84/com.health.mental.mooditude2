package com.health.mental.mooditude.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.health.mental.mooditude.R
import com.health.mental.mooditude.activity.ui.community.AddNewPostActivity
import com.health.mental.mooditude.activity.ui.community.AddPostCommentActivity
import com.health.mental.mooditude.activity.ui.community.PostDetailsActivity
import com.health.mental.mooditude.services.freshchat.ChatService
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.core.M3AssessmentManager
import com.health.mental.mooditude.custom.CustomBadgeDrawable
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.SharedPreferenceManager
import com.health.mental.mooditude.data.entity.*
import com.health.mental.mooditude.databinding.ViewAssessmentTopbarBinding
import com.health.mental.mooditude.databinding.ViewAssessmentTopbarSharingBinding
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.fragment.HelpOptionsFragment
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import com.health.mental.mooditude.services.instrumentation.*
import com.health.mental.mooditude.utils.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.indeterminateProgressDialog
import java.io.File
import java.util.*


/**
 * Created by Jayshree Rathod on 02,July,2021
 */
abstract class BaseActivity : AppCompatActivity(), HelpOptionsFragment.ItemClickListener {
    //For initializing views
    protected abstract fun initComponents()

    //used for logging purpose
    protected val TAG = this.javaClass.simpleName

    private var requestCode: Int = -1
    private var resultHandler: ActivityResultLauncher<Intent>? = null

    //private var mPhotoURI: Uri? = null

    private var logOutInProcess: Boolean = false
    private var deleteAccountInProcess: Boolean = false

    //for progress dialog
    private var mProgressDialog: ProgressDialog? = null

    //For photo
    protected var mPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)*/
        registerForActivityResult()

        //makeStatusBarTransparent()
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar))

        //log event
        EventCatalog.instance.viewedScreen(TAG)
    }

    fun Activity.makeStatusBarTransparent() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                statusBarColor = Color.TRANSPARENT
            }
        }*/

        /* val window: Window = getWindow()
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
         window.statusBarColor = ContextCompat.getColor(this, R.color.error_red)
         window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)*/

        val window = this.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.statusBarColor = this.resources.getColor(R.color.page_background)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    fun View.setMarginTop(marginTop: Int) {
        val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, marginTop, 0, 0)
        this.layoutParams = menuLayoutParams
    }

    private fun registerForActivityResult() {
        if (shouldRegisterForActivityResult()) {
            resultHandler =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult())
                { result ->

                    onActivityResult(result.data, requestCode, result.resultCode)
                    this.requestCode = -1
                }
        }
    }

    public fun startActivityForResult(requestCode: Int, intent: Intent) {
        this.requestCode = requestCode
        resultHandler?.launch(intent)
    }

    protected open fun onActivityResult(data: Intent?, requestCode: Int, resultCode: Int) {
        // For sub activities
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                REQUEST_ID_CAPTURE_IMAGE -> if (resultCode == RESULT_OK) {
                    if (this is ProfileActivity) {

                        //Uri of camera image
                        (this as ProfileActivity).updateImageFromCamera(mPhotoUri)
                    } else if (this is JournalNewEntryActivity) {

                        //Uri of camera image
                        (this as JournalNewEntryActivity).updateImageFromCamera(mPhotoUri)
                    } else if (this is AddPostCommentActivity) {

                        //Uri of camera image
                        (this as AddPostCommentActivity).updateImageFromCamera(mPhotoUri)
                    } else if (this is AddNewPostActivity) {

                        //Uri of camera image
                        (this as AddNewPostActivity).updateImageFromCamera(mPhotoUri)
                    }
                }
                REQUEST_ID_SELECT_IMAGE -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.data
                    if (selectedImage != null) {
                        if (this is ProfileActivity) {
                            (this as ProfileActivity).updateImage(selectedImage)
                        } else if (this is JournalNewEntryActivity) {
                            (this as JournalNewEntryActivity).updateImage(selectedImage)
                        } else if (this is AddPostCommentActivity) {
                            (this as AddPostCommentActivity).updateImage(selectedImage)
                        } else if (this is AddNewPostActivity) {
                            (this as AddNewPostActivity).updateImage(selectedImage)
                        }
                    }
                }

                REQUEST_ID_START_REGISTRATION -> if (resultCode == RESULT_OK) {
                    if (this is WelcomeActivity) {
                        finish()
                    } else if (this is OnBoardingActivity) {
                        setResult(RESULT_OK)
                        finish()
                    }
                }

                REQUEST_ID_START_ONBOARDING -> if (resultCode == RESULT_OK) {
                    if (this is WelcomeUserActivity) {
                        finish()
                    }
                }
                REQUEST_ID_START_ASSESSMENT -> {
                    if (resultCode == RESULT_OK) {
                        if (this is M3AssessmentActivity) {
                            (this as M3AssessmentActivity).finish()
                        }
                    }
                }
                REQUEST_ID_START_ASSESSMENT_STAGE1 -> {
                    if (resultCode == RESULT_OK) {
                        if (this is M3AssessmentStartActivity) {
                            (this as M3AssessmentStartActivity).finish()
                        }
                    } else if (resultCode == RESULT_ASSESSMENT_FINISHED) {
                        if (this is M3AssessmentStartActivity) {
                            //TELL ABOVE PAGE TO FINISH
                            setResult(RESULT_OK)
                            (this as M3AssessmentStartActivity).finish()
                        }
                    }
                }
                REQUEST_ID_START_ASSESSMENT_STAGE2 -> if (resultCode == RESULT_OK) {
                    if (this is ScoreCalculatingActivity) {
                        (this as ScoreCalculatingActivity).showAssessmentNextQuestion()
                    }
                }
                REQUEST_POST_DETAILS -> {
                    if (this is HomeActivity) {
                        (this as HomeActivity).postDetailsClosed(resultCode, data)
                    }
                }
                REQUEST_ADD_NEW_POST ->
                    if (resultCode == RESULT_OK) {
                        if (this is HomeActivity) {
                            (this as HomeActivity).newPostAdded(data)
                        }
                    }
                REQUEST_ADD_POST_COMMENT -> if (resultCode == RESULT_OK) {
                    if (this is PostDetailsActivity) {
                        (this as PostDetailsActivity).newCommentAdded(data)
                    } else if (this is HomeActivity) {
                        (this as HomeActivity).newCommentAdded(data)
                    }
                }
                REQUEST_ADD_POST_COMMENT_TO_REPORT -> if (resultCode == RESULT_OK) {
                    if (this is PostDetailsActivity) {
                        (this as PostDetailsActivity).newCommentAddedForReport()
                    }
                }
                REQUEST_EDIT_POST -> if (resultCode == RESULT_OK) {
                    if (this is PostDetailsActivity) {
                        (this as PostDetailsActivity).onEditPostCompleted(data)
                    }
                }
                REQUEST_EDIT_ENTRY -> if (resultCode == RESULT_OK) {
                    if (this is JournalActivity) {
                        (this as JournalActivity).onEditEntryCompleted(data)
                    }
                }
                REQUEST_CREATE_MOOD_ENTRY -> if (resultCode == RESULT_OK) {
                    if (this is OnBoardingActivity) {
                        (this as OnBoardingActivity).onMoodEntryCreated(data)
                    }
                }
                REQUEST_CREATE_GUIDED_ENTRY -> if (resultCode == RESULT_OK) {
                    if (this is OnBoardingActivity) {
                        (this as OnBoardingActivity).onGuidedEntryCreated(data)
                    }
                }
                REQUEST_SIGNUP_ONBOARDING -> if (resultCode == RESULT_OK) {
                    if (this is WelcomeActivity) {
                        finish()
                    }
                }
            }
        }
    }

    protected open fun shouldRegisterForActivityResult(): Boolean {
        // Sub activities that need the onActivityResult "mechanism", should override this and return true
        return true
    }

    /**
     * To shows back key in toolbar
     *
     * @param toolbar
     */
    fun initActionBar(toolbar: Toolbar, title: String = "", enabled: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(enabled)
        supportActionBar!!.setDisplayShowHomeEnabled(enabled)

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primaryColor))

        if (this is M3AssessmentScoreActivity ||
            this is M3AssessmentScoreFullActivity || this is TherapistFeedbackActivity || this is JournalPromptCatDetailsActivity
        ) {
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    fun setPageTitle(toolbar: Toolbar, title: String) {
        toolbar.title = title
    }


    /**
     * Add fragment into desire fragment container layout
     * @param fragmentContainerResourceId fragment container resource  id
     * *
     * @param nextFragment                the fragment which now we want to add above current fragment in same container
     * *
     * @return true if fragment added successfully, false otherwise
     * *
     * @throws IllegalStateException throws in case of transaction after activity saved its state
     */
    @Throws(IllegalStateException::class)
    protected fun addFragment(
        fragmentContainerResourceId: Int, nextFragment: Fragment, removeAll: Boolean = false,
        animationOn: Boolean = true, isDrawer: Boolean = false
    ): Boolean {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        //Show Animation
        if (animationOn) {
            fragmentTransaction.setCustomAnimations(
                R.anim.anim_right_in,
                R.anim.anim_left_out,
                R.anim.anim_left_in,
                R.anim.anim_right_out
            )
        } else if (isDrawer) {
            fragmentTransaction.setCustomAnimations(
                R.anim.zoom_enter,
                0
            )
        }


        if (removeAll) {
            for (fragment in supportFragmentManager.fragments) {
                if (fragment != null) {
                    fragmentTransaction.remove(fragment)
                }
            }

            //remove all back stack entries
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        fragmentTransaction.add(
            fragmentContainerResourceId,
            nextFragment,
            nextFragment.javaClass.simpleName
        )
        fragmentTransaction.commit()
        return true
    }


    /**
     * Add fragment into desire fragment container layout
     * @param fragmentContainerResourceId fragment container resource  id
     * *
     * @param currentFragment             current added fragment into same container
     * *
     * @param nextFragment                the fragment which now we want to add above current fragment in same container
     * *
     * @return true if fragment added successfully, false otherwise
     * *
     * @throws IllegalStateException throws in case of transaction after activity saved its state
     */
    @Throws(IllegalStateException::class)
    protected fun addFragment(
        fragmentContainerResourceId: Int, currentFragment: Fragment?,
        nextFragment: Fragment, animationOn: Boolean = true
    ): Boolean {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        //fragmentTransaction.setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out,R.anim.anim_right_in, R.anim.anim_right_out)
        if (animationOn) {
            fragmentTransaction.setCustomAnimations(
                R.anim.anim_right_in,
                R.anim.anim_left_out,
                R.anim.anim_left_in,
                R.anim.anim_right_out
            )
        }

        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        fragmentTransaction.add(
            fragmentContainerResourceId,
            nextFragment,
            nextFragment.javaClass.simpleName
        )
        fragmentTransaction.addToBackStack(nextFragment.javaClass.simpleName)

        fragmentTransaction.commit()
        return true
    }


    /**
     * Replace fragment into desire fragment container layout

     * @param fragmentContainerResourceId fragment container resource  id
     * *
     * @param nextFragment                the fragment which now we want to add above current fragment in same container
     * *
     * @return true if fragment added successfully, false otherwise
     * *
     * @throws IllegalStateException throws in case of transaction after activity saved its state
     */
    @Throws(IllegalStateException::class)
    protected fun replaceFragment(
        fragmentContainerResourceId: Int,
        nextFragment: Fragment?,
        customAnimation: Boolean = true
    ): Boolean {
        if (nextFragment == null) {
            return false
        }
        val fragmentManager = supportFragmentManager


        /*if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment)
            fragmentManager.popBackStack(null,0)
        }*/

        //fragmentTransaction.remove(currentFragment!!).commit()

        /*fragmentTransaction.remove(fragmentContainerResourceId, nextFragment)
        //fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()*/


        val ft = fragmentManager.beginTransaction()

        if (customAnimation) {
            ft.setCustomAnimations(
                R.anim.anim_right_in,
                R.anim.anim_left_out,
                R.anim.anim_left_in,
                R.anim.anim_right_out
            )
        }
        fragmentManager.popBackStack()
        ft.replace(fragmentContainerResourceId, nextFragment, nextFragment.javaClass.simpleName)
        ft.addToBackStack(nextFragment.javaClass.simpleName)
        ft.commit()
        return true
    }

    /**
     * Back key handling from ToolBar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //navigation button
            android.R.id.home -> {
                //supportFinishAfterTransition()
                onBackPressed()
                return true
            }

            R.id.menu_help -> {
                if (this is OnBoardingActivity || this is WelcomeUserActivity
                    || this is SetUserProfileActivity || this is M3AssessmentActivity
                ) {
                    // Do Fragment menu item stuff here
                    supportFragmentManager.let {
                        HelpOptionsFragment.newInstance(this).apply {
                            show(it, tag)
                        }
                    }
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(itemId: Int) {
        when (itemId) {
            R.id.menu_chat -> {
                ChatService.instance.show(this)
            }
            R.id.menu_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
    }


    /**
     * "Back" key handling
     */
    override fun onBackPressed() {
        super.onBackPressed()
        //add animation
        /*if (this is WelcomeActivity || this is SplashActivity) {
            overridePendingTransition(0, R.anim.anim_slide_out_bottom)
        } else {
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out)
        }*/
    }

    // Handled permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "Permission Requires Access to Camara.", Toast.LENGTH_SHORT
                )
                    .show()
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "Permission Requires Access to Your Storage.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (this is ProfileActivity || this is JournalNewEntryActivity
                    || this is AddPostCommentActivity || this is AddNewPostActivity
                ) {
                    selectImage()
                }

            }
        }
    }

    fun selectImage() {
        if (!UiUtils.checkAndRequestPermissions(this, REQUEST_ID_MULTIPLE_PERMISSIONS)) {
            return
        }
        val optionsMenu = arrayOf<CharSequence>(
            "Take Photo",
            "Choose from Gallery",
            "Exit"
        ) // create a menuOption Array

        // create a dialog for showing the optionsMenu
        // create a dialog for showing the optionsMenu
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        // set the items in builder
        // set the items in builder
        builder.setItems(optionsMenu, DialogInterface.OnClickListener { dialogInterface, i ->
            if (optionsMenu[i] == "Take Photo") {
                /*
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val file = File(Environment.getExternalStorageDirectory(), "pic.jpg")
                val uri = FileProvider.getUriForFile(
                    requireActivity(),
                    this.requireActivity().getPackageName().toString() + ".provider",
                    file
                )
                printLog("Uri : " + uri)
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                (requireActivity() as BaseActivity).startActivityForResult(REQUEST_ID_CAPTURE_IMAGE, takePicture)
                */

                //if phone android version greater or equal to Marshmelow
                // if android version >= 24 (Android Nugget)L
                var takePicture: Intent? = null
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //create new String to pictures directory and set the file name to current_time.jpg
                    val file =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            .toString() + "/profile.jpg";
                    val file1 = File(file)
                    //set GLOBAL variable Uri from the file using FileProvider
                    mPhotoUri = FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".provider",
                        file1
                    );

                    //add the file location the intent (the picture will be save to this file)
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                } else {

                    // same thing only without FileProvider (FileProvider only required since Nugget)
                    takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    val file =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            .toString() + "/profile.jpg"
                    val file1 = File(file);
                    mPhotoUri = Uri.fromFile(file1);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                }

                startActivityForResult(REQUEST_ID_CAPTURE_IMAGE, takePicture)

                /*
                // Open the camera and get the photo
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                (requireActivity() as BaseActivity).startActivityForResult(REQUEST_ID_CAPTURE_IMAGE, takePicture)
                */

            } else if (optionsMenu[i] == "Choose from Gallery") {
                // choose from  external storage
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(
                    REQUEST_ID_SELECT_IMAGE,
                    pickPhoto
                )
            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        })
        builder.show()
    }


    /**
     * Creates an account
     */
    protected fun loginToServer(userId: String?) {
        //showProgress
        //showProgress(R.string.loggin_in)
        if (userId == null) {
            //When signup
            onUserSignupOrLogin(true)
        } else {
            DBManager.instance.fetchUser(userId,
                object : FBQueryCompletedListener {

                    override fun onResultReceived(result: Any?) {
                        //showHomePage()
                        hideProgressDialog()
                        if (result != null) {
                            onUserSignupOrLogin(false)
                        }
                    }
                })
        }
    }


    private fun onUserSignupOrLogin(isSignup:Boolean) {
        //Now check for server configurations
        DBManager.instance.getServerConfigurations()

        //Check for user profile settings
        val user = DataHolder.instance.getCurrentUser()
        if (user != null) {

            if(isSignup) {
                EventCatalog.instance.onSignUp(user)
            }

            EventCatalog.instance.onLogIn(user)

            //attach with chathelper
            ChatService.instance.onUserIdChanged(user)

            //Set result so it should be closed
            if (this@BaseActivity is EmailRegistrationActivity) {
                //set result
                setResult(RESULT_OK)
            }
            if (!user.profileCompleted) {
                showWelcomeUserPage()
            } else {
                //Profile is already set, so now check for assessment
                //Let's first check for M3Assessment
                val isCompleted =
                    SharedPreferenceManager.getAssessmentCompleted()
                if (isCompleted != null && isCompleted) {
                    showHomePage(HomeActivity.ScreenMode.Home)
                } else {
                    showAssessmentPage()
                    //showHomePage(HomeActivity.ScreenMode.Home)
                }
            }
        }
    }


    /**
     * Show welcome page to sign-up/sign-in
     */
    fun showWelcomeUserPage() {
        val intent1 = Intent(this, WelcomeUserActivity::class.java)
        startActivity(intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        finish()
    }

    fun invitationCodeProccessed(code: String) {
        DBManager.instance.processInvitationCode(code)

        showWelcomeUserPage()
    }

    /**
     * Show welcome page to sign-up/sign-in
     */
    protected fun showHomePage(screenMode: HomeActivity.ScreenMode = HomeActivity.ScreenMode.Home) {

        //If signup user log an event
        if(EmailRegistrationActivity.gIsNewUser) {
            EventCatalog.instance.onSignUp(DataHolder.instance.getCurrentUser()!!)
        }

        val intent1 = Intent(this, HomeActivity::class.java)
        intent1.putExtra("mode", screenMode)

        //check for postId
        val extras = intent.extras
        if (extras != null && extras.containsKey(KEY_FCM_POST_ID)) {
            val postId = extras.getString(KEY_FCM_POST_ID)
            if (postId != null && postId.trim().isNotEmpty()) {
                //open postdetails page
                debugLog(TAG, "PostID : " + postId)
                intent1.putExtra(KEY_FCM_POST_ID, postId)
                intent1.putExtra("mode", HomeActivity.ScreenMode.Community)
            }
        }


        startActivity(intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        finish()
    }

    /**
     * Show welcome page to sign-up/sign-in
     */
    protected fun showAssessmentPage() {
        debugLog(TAG, "ShowshowAssessmentPage")
        val intent1 = Intent(this, M3AssessmentActivity::class.java)
        startActivity(intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        finish()
    }

    /**
     * Show welcome page to sign-up/sign-in
     */
    protected fun showScoreCalculatingPage(showHomePage: Boolean) {
        val intent1 = Intent(this, ScoreCalculatingActivity::class.java)
        intent1.putExtra("show_home", showHomePage)
        startActivity(intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        finish()
    }

    /**
     * Show welcome page to sign-up/sign-in
     */
    protected fun showAssessmentFlowDialog2(position: Int) {
        val intent1 = Intent(this, MAssessmentFlowDialog2::class.java)
        intent1.putExtra("position", position)
        startActivityForResult(REQUEST_ID_START_ASSESSMENT_STAGE2, intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        //finish()
    }

    /**
     * Show total score
     */
    protected fun showAssessmentScorePage(showHomePage: Boolean) {
        val intent1 = Intent(this, M3AssessmentScoreActivity::class.java)
        intent1.putExtra("show_home", showHomePage)
        startActivity(intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        finish()
    }

    /**
     * Show total score
     */
    fun showAssessmentScoreDetailedPage(showHomePage: Boolean) {
        val intent1 = Intent(this, M3AssessmentScoreFullActivity::class.java)
        intent1.putExtra("show_home", showHomePage)
        startActivity(intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        finish()
    }

    /**
     * Show page for user profile settings
     */
    protected fun setUserProfileSettings() {
        val intent1 = Intent(this, SetUserProfileActivity::class.java)
        startActivityForResult(REQUEST_ID_START_ONBOARDING, intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
        //finish()
    }

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                finish()
            }
        }

    fun showLoginPage() {
        val intent = Intent(this@BaseActivity, LoginActivity::class.java)
        resultLauncher.launch(intent)
        overridePendingTransition(R.anim.anim_slide_out_top, R.anim.anim_slide_in_bottom)
    }

    private fun logout() {
        val flag = DBManager.instance.logout()
        debugLog(TAG, "FLAG LOGOUT : " + flag)
        FirebaseAuth.getInstance().signOut()

        DataHolder.instance.logOut()

        //Clears data
        clearCache()

        //remove reminder
        CalendarUtils.cancelAlarmForUser(this)

        //Reset user data at logout
        ChatService.instance.resetUser()

        //Reset user data
        EventCatalog.instance.onLogout()
    }


    fun clearCache() {
        try {
            val list = databaseList()
            for (item in list) {

                if (item.startsWith("firestore.")) {
                    val file = File(getDatabasePath(item).absolutePath)
                    debugLog(TAG, "File : " + file.path + " : File exists : " + file.exists())
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * Shows custom progress dialog
     */
    internal fun showProgressDialog(resId: Int) {
        mProgressDialog = indeterminateProgressDialog(getString(resId))
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.show()
    }


    /**
     * Hides progress dialog
     */
    internal fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
    }

    /**
     * Logout with confirmation dialog
     */
    fun logOutUser() {
        alert(
            getString(R.string.do_u_want_to_logout),
            getString(R.string.logout)
        )
        {
            positiveButton(R.string.yes) {
                logOutInProcess = true
                showProgressDialog(R.string.please_wait)

                //For firebase
                FirebaseAuth.getInstance()
                    .addAuthStateListener(object : FirebaseAuth.AuthStateListener {
                        override fun onAuthStateChanged(p0: FirebaseAuth) {
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                // User is signed in
                            } else {
                                // User is signed out
                                //hideProgressDialog()
                                if (logOutInProcess) {
                                    logOutInProcess = false

                                    EventCatalog.instance.loggedOut()

                                    val intent = Intent(this@BaseActivity, HomeActivity::class.java)
                                    intent.putExtra(KEY_EXIT, true)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    })

                logout()
                hideProgressDialog()
            }
            negativeButton(R.string.no) {}
        }.show()
    }

    fun deleteUserAccount() {
        alert(
            getString(R.string.do_u_want_to_delete),
            getString(R.string.delete_account)
        )
        {
            positiveButton(R.string.yes) {

                deleteAccountInProcess = true
                showProgressDialog(R.string.please_wait)

                //For firebase
                //delete account from firebase
                if (FirebaseAuth.getInstance().currentUser != null) {
                    FirebaseAuth.getInstance().currentUser!!.delete()
                        .addOnSuccessListener {

                            if (deleteAccountInProcess) {
                                deleteAccountInProcess = false
                                EventCatalog.instance.accountDeleted()
                                logout()
                                hideProgressDialog()

                                val intent = Intent(this@BaseActivity, HomeActivity::class.java)
                                intent.putExtra(KEY_EXIT, true)
                                startActivity(intent)
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            debugLog(TAG, "DELETE Failure : " + it.localizedMessage)
                            hideProgressDialog()
                            UiUtils.showErrorToast(
                                this@BaseActivity,
                                it.localizedMessage.toString()
                            )
                        }
                }

            }
            negativeButton(R.string.no) {}
        }.show()
    }

    fun deleteAccount(password: String) {

        val auth = FirebaseAuth.getInstance()
        val currUser = auth.currentUser
        debugLog(TAG, "current user : "+ currUser)
        if (currUser != null && currUser.email != null) {
            debugLog(TAG, "current user : "+ currUser.email)
            showProgressDialog(R.string.please_wait)
            deleteAccountInProcess = true
            //First sign in
            auth.signInWithEmailAndPassword(currUser.email!!, password)
                .addOnSuccessListener {
                    //user deleted
                    debugLog(TAG, "User Signed: " + currUser.email)
                    currUser.delete()
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                if (deleteAccountInProcess) {
                                    deleteAccountInProcess = false
                                    //delete user
                                    ChatService.instance.deleteUser()
                                    logout()

                                    hideProgressDialog()

                                    val intent = Intent(this@BaseActivity, HomeActivity::class.java)
                                    intent.putExtra(KEY_EXIT, true)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                hideProgressDialog()
                                if(task.exception != null) {
                                    UiUtils.showErrorToast(
                                        this@BaseActivity,
                                        task.exception!!.localizedMessage.toString()
                                    )
                                }
                            }
                        }

                }
                .addOnFailureListener {
                    debugLog(TAG, "signin Failure : " + it.localizedMessage)
                    hideProgressDialog()
                    if(this is PreferencesActivity) {
                        (this as PreferencesActivity).showError(it)
                    }
                }
        }
    }

    fun startM3Assessment(showHomePage: Boolean) {
        val intent1 = Intent(this, M3AssessmentStartActivity::class.java)
        intent1.putExtra("show_home", showHomePage)
        startActivityForResult(REQUEST_ID_START_ASSESSMENT, intent1)
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
    }

    fun startM3AssessmentQuestions(showHomePage: Boolean) {
        val intent1 = Intent(this, MAssessmentFlowDialog::class.java)
        intent1.putExtra("show_home", showHomePage)
        startActivityForResult(REQUEST_ID_START_ASSESSMENT_STAGE1, intent1)
        overridePendingTransition(
            R.anim.anim_slide_out_top,
            R.anim.anim_slide_in_bottom
        )
    }

    fun checkForNewAssessment(layoutAddAssessment: View) {
        //check if view should be displayed or not

        DBManager.instance.getLastestAssessment().observe(this@BaseActivity, {
            val assessment = it
            if (assessment == null) {
                //No assessment found
                layoutAddAssessment.visibility = View.VISIBLE
                val tvDesc = layoutAddAssessment.findViewById<View>(R.id.tv_desc)
                if (tvDesc != null) {
                    tvDesc.visibility = View.GONE
                }
                layoutAddAssessment.setOnClickListener {
                    startM3Assessment(false)
                }
                return@observe
            }
            if (M3AssessmentManager.isAssessmentExpired(assessment!!)) {
                layoutAddAssessment.visibility = View.VISIBLE
                val tvDesc = layoutAddAssessment.findViewById<View>(R.id.tv_desc)
                if (tvDesc != null) {
                    tvDesc.visibility = View.VISIBLE
                }
                layoutAddAssessment.setOnClickListener {
                    startM3Assessment(false)
                }
            } else {
                layoutAddAssessment.visibility = View.GONE
            }
        }
        )


    }

    fun setupAssessmentTopbar(
        assessment: M3Assessment,
        assessmentTopbar: ViewAssessmentTopbarBinding
    ) {
        val allScore = assessment.allScore

        val intensity = M3AssessmentManager.getIntensityForAllScore(allScore)
        val color = M3AssessmentManager.getScoreBgColorID(intensity)

        assessmentTopbar.tvDate.text = CalendarUtils.formatDateForAssessment(assessment.createDate)
        //set score
        assessmentTopbar.tvScore.text = allScore.toString()
        assessmentTopbar.circleScore.setImageDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    color
                )
            )
        )
        assessmentTopbar.tvRisk.text = getString(M3AssessmentManager.getRiskTextID(intensity))

        val descText =
            String.format(getString(M3AssessmentManager.getRiskDescTextID(intensity)), allScore)
        if (Build.VERSION.SDK_INT >= 24) {
            assessmentTopbar.tvRiskDesc.setText(Html.fromHtml(descText, 0))
        } else {
            assessmentTopbar.tvRiskDesc.setText(Html.fromHtml(descText))
        }
    }

    fun setupAssessmentTopbarForSharing(
        assessment: M3Assessment,
        assessmentTopbar: ViewAssessmentTopbarSharingBinding
    ) {
        val allScore = assessment.allScore

        val intensity = M3AssessmentManager.getIntensityForAllScore(allScore)
        val color = M3AssessmentManager.getScoreBgColorID(intensity)

        assessmentTopbar.tvDate.text = CalendarUtils.formatDateForAssessment(assessment.createDate)
        //set score
        assessmentTopbar.tvScore.text = allScore.toString()
        assessmentTopbar.circleScore.setImageDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    color
                )
            )
        )
        assessmentTopbar.tvRisk.text = getString(M3AssessmentManager.getRiskTextID(intensity))

        val descText =
            String.format(getString(M3AssessmentManager.getRiskDescTextID(intensity)), allScore)
        if (Build.VERSION.SDK_INT >= 24) {
            assessmentTopbar.tvRiskDesc.setText(Html.fromHtml(descText, 0))
        } else {
            assessmentTopbar.tvRiskDesc.setText(Html.fromHtml(descText))
        }
    }

    fun onNeedTherapistBtnClicked() {
        val intent = Intent(this, FindMyTherapistActivity::class.java)
        startActivity(intent)
    }

    fun showTherapistFeedbackPage(requestId: String) {
        val intent = Intent(this, TherapistFeedbackActivity::class.java)
        intent.putExtra("id", requestId)
        startActivity(intent)
    }

    fun showJournalCatDetails(category: PromptCategory, imgView: ImageView) {
        val intent = Intent(this, JournalPromptCatDetailsActivity::class.java)
        intent.putExtra("title", category.title)
        intent.putExtra("short_text", category.subtitle)
        intent.putExtra("desc", category.description)
        intent.putExtra("url", category.imgStr!!)
        intent.putExtra("attachment", category.attachment)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            imgView,
            "image"
        )

        startActivity(intent, options.toBundle())

        //log an event
        EventCatalog.instance.viewedPromptCategoryDescription(category.categoryId)
    }

    fun showJournalPromptDetails(prompt: JournalPrompt, imgView: ImageView) {
        if (prompt.stepsStr == null || prompt.stepsStr!!.isEmpty()) {
            return
        }
        debugLog(TAG, "Prompt steps : " + prompt.stepsStr)
        val intent = Intent(this, JournalPromptDetailsActivity::class.java)
        intent.putExtra("prompt", Gson().toJson(prompt))

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            imgView,
            "image"
        )
        startActivity(intent, options.toBundle())
    }

    fun addNewJournalEntry() {
        val intent = Intent(this, JournalNewEntryActivity::class.java)
        startActivity(intent)
    }

    /**
     * Exit the application and remove the process
     */
    protected fun exitTheApplication() {
        debugLog(TAG, "Exit the application ")
        moveTaskToBack(true);
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(KEY_EXIT, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        this.finish()
    }

    protected fun showSignUpWelcomePage() {
        startActivity(Intent(this, WelcomeActivity::class.java))
    }

    protected fun showUnreadMsgCount(menu:Menu, count: Int) {
        val itemCart: MenuItem = menu.findItem(R.id.menu_help)
        val icon = itemCart.icon as LayerDrawable
            setBadgeCount(this, icon, count.toString())
        //log event
        EventCatalog.instance.badgeCreatedEvent(TAG, count)
    }

    private fun setBadgeCount(context: Context, icon: LayerDrawable, count: String) {
        val badge: CustomBadgeDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_badge)
        if (reuse != null && reuse is BadgeDrawable) {
            badge = reuse as CustomBadgeDrawable
        } else {
            badge = CustomBadgeDrawable(context)
        }
        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_badge, badge)
        icon.invalidateSelf()
    }
}