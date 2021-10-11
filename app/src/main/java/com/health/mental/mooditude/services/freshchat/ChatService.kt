package com.health.mental.mooditude.services.freshchat

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.freshchat.consumer.sdk.*
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.DBManager
import com.health.mental.mooditude.data.model.AppUser
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.listener.FreshChatListener
import com.health.mental.mooditude.services.instrumentation.EventCatalog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Jayshree Rathod on 27,September,2021
 */
class ChatService private constructor(private val mContext: Application) {

    //used for logging purpose
    protected val TAG = this.javaClass.simpleName

    private val mFreshChat: Freshchat

    companion object {

        lateinit var instance: ChatService

        fun createService(context: Application) {
            instance = ChatService(context)
            instance.initSDK()
        }
    }

    init {
        mFreshChat = Freshchat.getInstance(mContext)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val restoreId = mFreshChat.user.restoreId
            debugLog(
                TAG,
                "Restore ID received in receiver : " + restoreId + " : " + restoreId.length
            )

            //update
            val user = DataHolder.instance.getCurrentUser()
            if (user != null) {
                updateRestoreId(user)
            }
        }
    }

    private val unreadCountChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mFreshChat.getUnreadCountAsync(object : UnreadCountCallback {
                override fun onResult(
                    freshchatCallbackStatus: FreshchatCallbackStatus?,
                    unreadCount: Int
                ) {
                    debugLog(
                        TAG,
                        "unreadcount broadcast received : " + unreadCount
                    )
                    for (listener in mListenerList) {
                        listener.getUnreadCountReceived(unreadCount)
                    }
                }

            });
        }
    }

    fun addUnreadCountListener(listener: FreshChatListener) {
        if (!mListenerList.contains(listener)) {
            mListenerList.add(listener)
        }
    }

    fun removeUnreadCountListener(listener: FreshChatListener) {
        this.mListenerList.remove(listener)
    }

    private fun initSDK() {

        /*
        // Production
        fchatConfig = FreshchatConfig.init(appID: "bea229e5-445c-44bb-ad55-c592e41dde3d",
                                               andAppKey: "8b7066b8-15f5-4f95-8edb-8ad949277dd6")

        #if DEBUG
//        // Test
        fchatConfig = FreshchatConfig.init(appID: "ff19da91-a820-4542-9b85-ff240154f638",
                                               andAppKey: "5cf80914-3f2c-4317-8dec-fa6083d48a98")
        #endif
         */
        //Production
        var freshchatConfig = FreshchatConfig(
            "bea229e5-445c-44bb-ad55-c592e41dde3d",
            "8b7066b8-15f5-4f95-8edb-8ad949277dd6"
        )

        if (BuildConfig.DEBUG) {
            freshchatConfig = FreshchatConfig(
                "ff19da91-a820-4542-9b85-ff240154f638",
                "5cf80914-3f2c-4317-8dec-fa6083d48a98"
            )
        }
        freshchatConfig.setDomain("msdk.freshchat.com")
        freshchatConfig.setCameraCaptureEnabled(true);
        freshchatConfig.setGallerySelectionEnabled(true);
        freshchatConfig.setResponseExpectationEnabled(true);
        mFreshChat.init(freshchatConfig)

        //add broadcast
        val intentFilter = IntentFilter(Freshchat.FRESHCHAT_UNREAD_MESSAGE_COUNT_CHANGED)
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
            unreadCountChangeReceiver,
            intentFilter
        )
    }

    fun sendMessage(content: String, tag: String = "The Tag") {
        val freshchatMessage = FreshchatMessage()
        freshchatMessage.message = content
        freshchatMessage.tag = tag

        Freshchat.sendMessage(mContext, freshchatMessage)
    }

    fun show(activityContext: Activity, showChat: Boolean = true) {
        EventCatalog.instance.chatStarted()
        if (showChat) {
            Freshchat.showConversations(activityContext)
        }
    }

    fun onUserIdChanged(user: AppUser) {
        debugLog(TAG, "onUserIdChanged ")

        //register broadcast receiver for restoreID
        val intentFilter1 = IntentFilter(Freshchat.FRESHCHAT_USER_RESTORE_ID_GENERATED)
        LocalBroadcastManager.getInstance(mContext)
            .registerReceiver(broadcastReceiver, intentFilter1)

        //if user.isLocal { return } // we're not recording local users
        val freshChatUser = mFreshChat.user
        freshChatUser.firstName = user.getFirstName()
        freshChatUser.lastName = user.getLastName()
        freshChatUser.email = user.email

        //debugLog(TAG, "FirstName : " + mFreshchatUser.firstName + " : " + mFreshchatUser.lastName)

        mFreshChat.setUser(freshChatUser)
        mFreshChat.identifyUser(user.userId, user.freshChatRestoreID)

        debugLog(TAG, "UserID freshChatRestoreID : " + user.freshChatRestoreID)
        if (user.freshChatRestoreID.isNullOrEmpty()) {
            // This message should be logged only the first time
            var joinedDate = user.memberSince
            if (joinedDate == 0L) {
                joinedDate = System.currentTimeMillis()
            }

            sendMessage(
                "Logged in at: " + SimpleDateFormat("M/d/yy, hh:mm:ss a zzzz", Locale.US).format(
                    joinedDate
                ), "Welcome"
            )
            debugLog(TAG, "User had sent first message : ")
        }

        //restore
        //mFreshChat.restoreUser(user.freshChatRestoreID!!)

        updateRestoreId(user)
    }

    private fun updateRestoreId(user: AppUser) {
        val restoreId = mFreshChat.user.restoreId
        debugLog(TAG, " updateRestoreId : " + restoreId + " : " + restoreId.length)
        if (!restoreId.isNullOrEmpty()) {
            if (user.freshChatRestoreID.isNullOrEmpty()) {
                //user.setFreshChatRestoreId(mFreshchatUser.restoreId)
                DBManager.instance.setFreshChatRestoreId(restoreId)
                user.freshChatRestoreID = restoreId
            }

            mFreshChat.identifyUser(user.userId, user.freshChatRestoreID)
        }
    }

    fun deleteUser() {
        setUserProperty("deleted", "true")
    }

    //logout
    fun resetUser() {
        Freshchat.resetUser(mContext)
        //unregister receiver
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(broadcastReceiver);
    }


    /*func registerForNotifications() {

        NotificationCenter.default.addObserver(self,selector: #selector(userRestoreIdReceived),name: NSNotification.Name(rawValue: FRESHCHAT_USER_RESTORE_ID_GENERATED),object: nil)

        // Register for unread message counts
        NotificationCenter.default.addObserver(self, selector: #selector(self.methodOfReceivedNotification(notification:)), name: Notification.Name(FRESHCHAT_UNREAD_MESSAGE_COUNT_CHANGED), object: nil)
    }


    @objc func methodOfReceivedNotification(notification: Notification){
        checkForUnreadMessages()
    }

    func checkForUnreadMessages(){
        freshchat.unreadCount { (count:Int) -> Void in

                self.raiseNotification(count: self.unreadMessagesCount > count ? self.unreadMessagesCount : count)
        }
    }

    func raiseNotification(count: Int) {

        self.unreadMessagesCount = count
        Run.onMain {
            UIApplication.shared.applicationIconBadgeNumber = count
            NotificationCenter.default.post(name: Constant.NotificationId.reloadView, object: "readCountChanged")
        }
    }*/


    fun trackEvent(name: String, properties: Map<String, Any>) {
        //mFreshChat.
        Freshchat.trackEvent(mContext, name, properties)
    }

    fun setUserProperty(key: String, value: String) {
        mFreshChat.setUserProperty(key, value)
    }

    fun registerFCMToken(deviceToken: String) {
        mFreshChat.setPushRegistrationToken(deviceToken)
    }

    fun removeFCMToken() {
        mFreshChat.setPushRegistrationToken("")
    }

    fun canHandleRemoteNotification(userInfo: HashMap<Any, *>): Boolean {
        //return mFreshChat.isFreshchatNotification(userInfo)
        return true
    }
    /*func handleRemoteNotification(userInfo: [AnyHashable : Any], onComplete: ()-> Void){
        if freshchat.isFreshchatNotification(userInfo) {
            freshchat.handleRemoteNotification(userInfo, andAppstate: UIApplication.shared.applicationState)
            onComplete()
        } else {
            onComplete()
        }
    }*/

    private var mListenerList = ArrayList<FreshChatListener>()
    fun getUnReadCount(listener: FreshChatListener) {
        mFreshChat.getUnreadCountAsync { freshchatCallbackStatus, unreadCount -> //Assuming "badgeTextView" is a text view to show the count on
            debugLog(TAG, "UnReadCount received : " + unreadCount)
            listener.getUnreadCountReceived(unreadCount)
        }
    }

    fun exitApp() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(unreadCountChangeReceiver);
    }
}