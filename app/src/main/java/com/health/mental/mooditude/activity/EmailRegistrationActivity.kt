package com.health.mental.mooditude.activity

import android.os.Bundle
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.health.mental.mooditude.R
import com.health.mental.mooditude.databinding.ActivityEmailRegistrationBinding
import com.health.mental.mooditude.fragment.*
import com.health.mental.mooditude.fragment.registration.*
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import com.health.mental.mooditude.services.instrumentation.resetPassword
import com.health.mental.mooditude.services.instrumentation.resetPasswordEmailSent

/**
 * Used for registration with email/password
 */
class EmailRegistrationActivity : RegistrationActivity() {

    private enum class ScreenMode {
        Name,
        Email,
        Login,
        ForgotPassword,
        ResetPassword
    }

    private var mDisplayName: String = ""
    private var mPromoCode: String = ""
    private var mEmail: String = ""
    private var mPassword: String = ""

    //private var mPhotoUri: Uri? = null
    private var mScreenMode: ScreenMode = ScreenMode.Name
    private lateinit var binding: ActivityEmailRegistrationBinding

    companion object {
        var gIsNewUser = false
    }

    /**
     * Called when any activity is being created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActionBar(findViewById(R.id.toolbar))
        initComponents()
    }


    /**
     * Initializes UI components
     */
    override fun initComponents() {
        val fragment = NameFragment()
        addFragment(R.id.layout_container, fragment, true)
    }

    fun onSignupBtnClicked(name: String, email: String, password: String) {
        mDisplayName = name
        mEmail = email
        mPassword = password
        createAccountWithEmailAuthProvider(mDisplayName, mEmail, mPassword)
    }

    fun showCloseBtn(backButton: Boolean = false) {
        if(backButton) {
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_forward)
        }
        else {
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)
        }

        //supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        //supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(false)
    }

    override fun askForInvitationCode() {
        //now add fragment for email/password
        /*addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(NameFragment::class.java.simpleName),
            EmailFragment(),
            true
        )
        mScreenMode = ScreenMode.Email
        setPageTitle(findViewById(R.id.toolbar), "")*/
        /*
        val intent1 = Intent(this, PromoCodeDialogActivity::class.java)
        startActivity(intent1)
        finish()*/

        onDontHaveCodeBtnClicked()
    }

    /**
     * Called when next button is clicked
     */
    /*fun onNextButtonClicked(name: String, code: String) {
        //binding.layoutContainer.etName.
        this.mDisplayName = name
        this.mPromoCode = code

        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentByTag(NameFragment::class.java.simpleName),
            EmailFragment(),
            true
        )
        mScreenMode = ScreenMode.Email
        setPageTitle(findViewById(R.id.toolbar), getString(R.string.create_account))
    }*/

    /**
     * Called when next button is clicked
     */
    fun onLoginButtonClicked() {

        //now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentById(R.id.layout_container), LoginFragment(),
            true
        )
        mScreenMode = ScreenMode.Login
        setPageTitle(findViewById(R.id.toolbar), getString(R.string.login))
    }

    fun onForgotPwdClicked() {
//now add fragment for email/password
        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentById(R.id.layout_container),
            ForgotPasswordFragment(),
            true
        )
        mScreenMode = ScreenMode.ForgotPassword
        setPageTitle(findViewById(R.id.toolbar), getString(R.string.reset_pwd))
        //log event
        EventCatalog.instance.resetPassword()
    }

    fun showResetPasswordPage() {
        //First remove current fragment
        onBackPressed()

        addFragment(
            R.id.layout_container,
            supportFragmentManager.findFragmentById(R.id.layout_container), ResetPasswordFragment(),
            true
        )
        mScreenMode = ScreenMode.ResetPassword
        setPageTitle(findViewById(R.id.toolbar), getString(R.string.login))

        //log event
        EventCatalog.instance.resetPasswordEmailSent()
    }

    /**
     * Called when next button is clicked
     */
    fun onLoginUser(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password) as EmailAuthCredential
        signInWithEmailAuthProvider(credential)
    }

    fun onForgotPassword(email: String) {
        sendEmailToResetPassword(email)
    }

    fun onDontHaveCodeBtnClicked() {
        //showWelcomeUserPage()
        loginToServer(null)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val fragment = supportFragmentManager.findFragmentById(R.id.layout_container)

        if (mScreenMode == ScreenMode.Email) {
            if (fragment is EmailFragment) {
                /*super.onBackPressed()
                mScreenMode = ScreenMode.Name
                setPageTitle(findViewById(R.id.toolbar), "")
                showCloseBtn(true)*/
                onDontHaveCodeBtnClicked()
            }
        } else if (mScreenMode == ScreenMode.Login) {
            if (fragment is LoginFragment) {
                super.onBackPressed()
                mScreenMode = ScreenMode.Name
                setPageTitle(findViewById(R.id.toolbar), "")
            }
        } else if (mScreenMode == ScreenMode.ForgotPassword) {
            if (fragment is ForgotPasswordFragment) {
                super.onBackPressed()
                mScreenMode = ScreenMode.Login
                setPageTitle(findViewById(R.id.toolbar), getString(R.string.login))
            }
        } else {
            super.onBackPressed()
        }

    }

    /*fun updateImage(selectedImage: Bitmap) {
        val fragment = supportFragmentManager.findFragmentById(R.id.layout_container)
        if (fragment is EmailFragment) {
            (fragment as EmailFragment).updateImage(selectedImage)
        }
    }
*/
    override fun showError(exception: Exception) {
        val fragment = supportFragmentManager.findFragmentById(R.id.layout_container)
        if (fragment is BaseFragment) {
            (fragment as BaseFragment).showError(exception)
        }
    }


}