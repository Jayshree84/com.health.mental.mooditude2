package com.health.mental.mooditude.cloudfunction

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener

/**
 * Created by Jayshree Rathod on 30,July,2021
 */
class FBFunctionsHelper private constructor() {

    private val TAG = javaClass.simpleName
    private val mFBFunctions: FirebaseFunctions

    private val PATH_VALIDATE_INVITATION_CODE   =   "invitationCodeUsed"
    private val PATH_PROCESS_INVITATION_CODE   =   "processInvitationCode"

    init {
        //initialize()
        //Enable offline data
        mFBFunctions = Firebase.functions
    }

    //private members
    private object Holder {
        val INSTANCE = FBFunctionsHelper()
    }

    companion object {
        private val CONFIGURATIONS_PATH = "configurations"

        val instance: FBFunctionsHelper by lazy { Holder.INSTANCE }
    }

    fun validateInvitationCode(code: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "code" to code,
            "push" to true
        )

        return mFBFunctions
            .getHttpsCallable(PATH_VALIDATE_INVITATION_CODE)
            .call(code)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }

    fun processInvitationCode(code: String, listener:FBQueryCompletedListener){
        // Create the arguments to the callable function.
        val userId = DataHolder.instance.getCurrentUser()!!.userId
        val data = hashMapOf(
            "code" to code,
            "userId" to userId,
            "platform" to "Android"
        )

        mFBFunctions
            .getHttpsCallable(PATH_PROCESS_INVITATION_CODE)
            .call(data)
            .addOnCompleteListener {
                val task = it
                debugLog(TAG, "Task return : " + task.toString() + " Task successful : " + task.isSuccessful)

                if(task.isSuccessful && task.result != null) {
                    val result = task.result?.data
                    debugLog(TAG, "Result : " + result.toString())
                }
                //task
                listener.onResultReceived(task)
            }

    }
}