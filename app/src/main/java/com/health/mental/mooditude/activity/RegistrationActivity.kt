package com.health.mental.mooditude.activity

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.health.mental.mooditude.R
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.AppUser
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import org.jetbrains.anko.indeterminateProgressDialog
import java.util.*


/**
 * Created by Jayshree.Rathod on 04-09-2017.
 */
abstract class RegistrationActivity : BaseActivity() {

    //private members
    protected lateinit var mAuth: FirebaseAuth
    protected var mIsNotifyInProgress: Boolean? = false
    //protected var mFirebaseUser: FirebaseUser? = null


    /**
     * Called when any activity is being created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
    }


    /**
     * Creates an account
     */
    protected fun createAccountOnServer(firebaseUser: FirebaseUser, name: String) {
        //showProgress
        showProgress(R.string.saving_information)

        updateProfileData(firebaseUser,name)
        //Let's first upload pic
        /*FirebaseStorageHelper.instance.uploadFile(this, photoUri,
            firebaseUser.uid,
            object : FirebaseStorageHelper.OnProgressStatusListener {
                override fun onCompleted(argument: Any?) {
                    printLog("onCompleted Photo upload : ")

                    if (argument != null) {
                        printLog("Photo URL : " + argument.toString())
                        val photoUrl = argument.toString()
                        updateProfileData(firebaseUser,name, photoUrl)
                    }
                    else {
                        updateProfileData(firebaseUser,name, "")
                    }
                }
            }, null)*/
    }

    private fun updateProfileData(firebaseUser: FirebaseUser, name:String) {
        //make asynchronous call
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        //First update profile info
        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(this) { task ->
            hideProgress()
            if (task.isSuccessful) {
                //show home page
                //Persist user
                val fbUser = mAuth.currentUser
                if (fbUser != null) {
                    val appUser = AppUser(fbUser)
                    //update user
                    DataHolder.instance.setCurrentUser(appUser)
                    setResult(Activity.RESULT_OK)

                    EmailRegistrationActivity.gIsNewUser = true
                    DBManager.instance.writeNewUser(appUser,
                        object : FBQueryCompletedListener {

                            override fun onResultReceived(result: Any?) {
                                askForInvitationCode()
                            }
                        })
                }
            } else {
                if(task.exception != null && this is EmailRegistrationActivity) {
                    val activity = this as EmailRegistrationActivity
                    activity.showError(task.exception!!)
                }
            }
        }
    }

    open fun askForInvitationCode() {

    }


    private var mProgressDlg: Dialog? = null
    fun hideProgress() {
        //First dismiss the old dialog
        if (mProgressDlg != null) {
            mProgressDlg!!.dismiss()
        }
    }

    fun showProgress(resId: Int) {
        hideProgress()
        mProgressDlg = indeterminateProgressDialog(getString(resId))
    }


    /**
     * Links the existing logged user with EmailAuthProvider credential
     * it returns success only if same user with existing credentials do not exist
     */
    protected fun createAccountWithEmailAuthProvider(
        name: String,
        email: String,
        password: String
    ) {
        //update progress
        showProgress(R.string.creating_account)

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    hideProgress()
                    if (task.result != null) {
                        val firebaseUser = task.result!!.user

                        if(firebaseUser != null) {
                            createAccountOnServer(firebaseUser, name)
                        }
                    }
                } else {
                    hideProgress()
                    showError(task.exception!!)
                }
            }
    }


   protected fun sendEmailToResetPassword(email: String) {
        showProgress(R.string.please_wait)
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                hideProgress()
                if(it.isSuccessful) {

                    if(this is EmailRegistrationActivity) {
                        (this as EmailRegistrationActivity).showResetPasswordPage()
                    }
                }
                else {
                    showError(it.exception!!)
                }
            }
    }

    /**
     * Sign In with Email Auth credentials
     */
    protected fun signInWithEmailAuthProvider(
        credential: EmailAuthCredential
    ) {
        showProgress(R.string.signin_in)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //hideProgress()
                    if (task.result != null) {
                        debugLog(TAG,
                            "signInWithEmailAuthProvider:success : " +
                                    task.result!!.user!!.displayName
                        )

                        if (task.result != null) {
                            val fbUser = task.result!!.user
                            if (fbUser != null) {
                                val userId = fbUser.uid
                                setResult(Activity.RESULT_OK)

                                EmailRegistrationActivity.gIsNewUser = false
                                loginToServer(userId)
                                //hideProgress()
                            }
                        }
                    }
                    else {
                        hideProgress()
                    }

                } else {
                    errorLog(TAG, "signInWithEmailAuthProvider:failure " + task.exception)
                    hideProgress()
                    showError(task.exception!!)
                }
            }
    }

    abstract fun showError(exception: Exception)


    /**
     * Verifies phone number using firebase API
     */
    /*protected fun verifyPhoneNumber(phoneNumber: String) {
        if (mIsNotifyInProgress as Boolean) {
            //wait it's in progress
            return
        }
        mIsNotifyInProgress = true
        mPhoneAuthCredential = null

        mAuthProviderInfo.phoneNumber = phoneNumber
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                mCallBacks)        // OnVerificationStateChangedCallbacks

        //update progress info
        showProgress(R.string.verifying_phone_number)
    }*/


    /**
     * When instance is persisted
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState?.putBoolean("mIsNotifyInProgress", mIsNotifyInProgress as Boolean)
        super.onSaveInstanceState(outState)
    }


    /**
     * Callback received by PhoneAuthProvider
     */
    /*private val mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        *//*
            This callback will be invoked in two situations:
         1 - Instant verification. In some cases the phone number can be instantly
             verified without needing to send or enter a verification code.
         2 - Auto-retrieval. On some devices Google Play services can automatically
             detect the incoming verification SMS and perform verificaiton without
             user action.*//*
        override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
            //set it to false
            mIsNotifyInProgress = false

            //save credential for future use
            mPhoneAuthCredential = p0

            //add logs
            ParasAnalytics.instance.phoneVerified()

            showProgress(R.string.phone_number_verified)
            if (this@RegistrationActivity is PhoneRegistrationActivity) {
                signInWithPhoneAuthCredential(p0 as PhoneAuthCredential)
            } else if (this@RegistrationActivity is SocialAccountRegistrationActivity) {
                handlePhoneNumber(p0 as PhoneAuthCredential)
            }
        }


        *//*
            This callback is invoked in an invalid request for verification is made,
            for instance if the the phone number format is not valid.
        *//*
        override fun onVerificationFailed(e: FirebaseException?) {
            mIsNotifyInProgress = false

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
                //printLogWithToast(this@PhoneRegistrationActivity, "Invalid Request")
                showAlert(getString(R.string.invalid_phonenumber), getString(R.string.enter_valid_phone_number))
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
                //printLogWithToast(this@PhoneRegistrationActivity, "The SMS quota for the project has been exceeded")
                showAlert(getString(R.string.too_many_requests), getString(R.string.sms_quota_exceeded))
            } else {
                if (e != null) {
                    showAlert(getString(R.string.error), e.localizedMessage)
                }
            }
            hideProgress()
        }


        override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
            mIsNotifyInProgress = false
            super.onCodeSent(p0, p1)
            if (mAuthProviderInfo.mode == SCREEN_MODE.PASSCODE_VERIFICATION) {
                return
            }

            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            //Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            //mVerificationId = verificationId;
            //mResendToken = token;
            //printLogWithToast(this@PhoneRegistrationActivity, "onCodeSent ")

            //Verification code is sent, now prompt the ui to get number from user sent through SMS
            //you can use the verification code and the verification ID that was passed to the method to create
            // a PhoneAuthCredential object, which you can in turn use to sign in the user
            //val credential = PhoneAuthProvider.getCredential(verificationId, code)

            // Save verification ID and resending token so we can use them later
            mVerificationId = p0
            mResendToken = p1

            mAuthProviderInfo.mode = SCREEN_MODE.PASSCODE_VERIFICATION
            setTimerForResendCodeButton()
            if (this@RegistrationActivity is PhoneRegistrationActivity) {
                addFragment(R.id.layout_container, supportFragmentManager.findFragmentByTag(PhoneNumberFragment::class.java.simpleName),
                        PasscodeFragment(mAuthProviderInfo.phoneNumber))
            } else if (this@RegistrationActivity is SocialAccountRegistrationActivity) {
                addFragment(R.id.layout_container, supportFragmentManager.findFragmentByTag(ConfirmInformationFragment::class.java.simpleName)
                        , PasscodeFragment(mAuthProviderInfo.phoneNumber))
            }

            setNextEnabled(false)

            //hide progress
            hideProgress()
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String?) {
            mIsNotifyInProgress = false

            super.onCodeAutoRetrievalTimeOut(p0)
            //printLogWithToast(this@PhoneRegistrationActivity, "onCodeAutoRetrievalTimeOut :  " + p0)

            //hide progress
            //hideProgress()
        }
    }*/

    var mTimer: Timer? = null
    var mTimerTask: TimerTask? = null
    /*private fun setTimerForResendCodeButton() {
        if (mTimer != null) {
            mTimer!!.cancel()
        }
        if (mTimerTask != null) {
            mTimerTask!!.cancel()
        }
        mTimer = Timer()
        mTimerTask = object : TimerTask() {
            override fun run() {
                if (mAuthProviderInfo.mode == SCREEN_MODE.PASSCODE_VERIFICATION) {
                    runOnUiThread(object : Runnable {
                        override fun run() {
                            btn_resend_code.visibility = View.VISIBLE
                        }

                    })
                }
            }
        }
        mTimer!!.schedule(mTimerTask, RESEND_CODE_BUTTON_TIMEOUT)
    }*/


    /**
     * Enables/disables NEXT button
     */
    fun setNextEnabled(flag: Boolean) {
        //btn_next.isEnabled = flag
    }


    /**
     * Called when back key is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()

        //printLog("OnBackPressed mAuthProviderInfo.mode : " + mAuthProviderInfo.mode )
        //handle screen mode
    }

    /**
     * Called when activity is destroyed
     */
    override fun onDestroy() {
        if (mTimer != null) {
            mTimer!!.cancel()
        }
        if (mTimerTask != null) {
            mTimerTask!!.cancel()
        }

        hideProgress()
        super.onDestroy()
    }

}