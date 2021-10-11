package com.health.mental.mooditude.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.health.mental.mooditude.core.DataHolder
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.model.*
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import com.health.mental.mooditude.listener.FBQueryCompletedListener
import java.util.*


/**
 * Created by Jayshree Rathod on 10,July,2021
 */
class FBAccountRepository(private val mAppDb: AppDatabase,
                          private val rdb: DatabaseReference,
                          private val mFireStore: FirebaseFirestore
) {
    private val TAG = this.javaClass.simpleName
    private var listener: ListenerRegistration? = null
    private val USERS_PATH = "users"
    private val USERS_STORE_PATH = "Users"
    private val SUBSCRIBERS_STORE_PATH = "Subscribers"
    private var profileRdb:DatabaseReference? = null

    // Listners are added at the start of the app and when user changes (create account, log in etc.)
    fun addListeners() {
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            return
        }
        //registerProfileChangeListner()
        registerSubscriptionDataListner()
    }

    // Listner are removed when user logs out or delete account
    fun removeListeners(){
        listener?.remove()
        listener = null

    }

    // Register for changes at the start of the app or when user changes
    fun registerProfileChangeListner(listener1: FBQueryCompletedListener): ValueEventListener? {
        val user1 = DataHolder.instance.getCurrentUser()
        if(user1 == null) {
            return null
        }
        val profileRdb = rdb.child(USERS_PATH).child(user1.userId)
        profileRdb.keepSynced(true)
        // also add the observer
        return profileRdb.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                debugLog(TAG, "event change : Profile")
                if(snapshot.exists()) {
                    // do something with the individual "user"
                    val user = snapshot.getValue(AppUser::class.java)!!
                    user.userId = snapshot.key.toString()
                    DataHolder.instance.setCurrentUser(user)
                    listener1.onResultReceived(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun registerSubscriptionDataListner(){
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            return
        }
        removeListeners()
        val docRef = mFireStore.collection(SUBSCRIBERS_STORE_PATH)
            .document(user.userId)
        
        listener = docRef.addSnapshotListener { value, error ->
            if(error != null) {
                errorLog(TAG, "Error in subscription data : " + error.localizedMessage)
                return@addSnapshotListener
            }

            if(value != null && value.exists()) {
                val data = value.data
                if(data != null) {
                    user.updateSubscription(data)
                }
            }
        }
    }

    // Get subscription data. Useful when applying promocode
    fun getSubscriptionData(){
        val user = DataHolder.instance.getCurrentUser()

        if(user == null) {
            return
        }

        val docRef = mFireStore.collection(SUBSCRIBERS_STORE_PATH)
            .document(user.userId)

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                debugLog(TAG, "Exist or not : " + documentSnapshot.exists())

                if (documentSnapshot.exists()) {
                    // do something with the individual "user"
                    val data = documentSnapshot.data
                    if(data != null) {
                        user.updateSubscription(data)
                    }
                }
            }
            .addOnFailureListener {
                errorLog(TAG, "Exception in subscribers query : " + it.localizedMessage)
            }

    }

    fun getUserById(userId: String, listener: FBQueryCompletedListener) {
        debugLog(TAG,"getUserById : " + userId)
        //val userId = "1tRP5oGa3bZ9hS1Y5z0897LxN9l2"
        //printLog("New userId : " + userId)

        val query = rdb.child(USERS_PATH).child(userId)
        query.keepSynced(true)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                debugLog(TAG,"Exist or not : " + dataSnapshot)
                debugLog(TAG,"Exist or not : " + dataSnapshot.exists() + " : " + dataSnapshot.childrenCount)

                if (dataSnapshot.exists()) {
                    // do something with the individual "user"
                    listener.onResultReceived(dataSnapshot.getValue(AppUser::class.java)!!)
                } else {
                    listener.onResultReceived(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                debugLog(TAG,"onCancelled : " + databaseError.message)
                listener.onResultReceived(null)
            }
        })
    }


    fun writeNewUser(
        user: AppUser,
        listener1: FBQueryCompletedListener
    ) {
        debugLog(TAG, "New User : " + user.email + " : " + user.name + " : " + user.userId)
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).keepSynced(true)

        //check for created date - 01 Jan 2018
        if (user.memberSince == 0L) {
            user.memberSince = System.currentTimeMillis()
        }
        rdb.child(USERS_PATH).child(user.userId).setValue(user)

        //Now update firestore too
        //convert to map
        val userProfile = UserProfile(user.userId, user.name, user.photo)

        mFireStore.collection(USERS_STORE_PATH)
            .document(user.userId)
            .set(userProfile)
            .addOnSuccessListener { debugLog(TAG,"Completed store : SUCCESS") }
            .addOnFailureListener { e -> errorLog(TAG, "Error writing document" + e.toString()) }


        //Let's attach FCM Token too if available
        //Pass it to Firebase database too
        val token = DataHolder.instance.getFCMToken()
        debugLog(TAG, "FCM token available : " + token)
        if(token.isNotEmpty()) {
            setFcmToken(token)
        }

        //set user in database
        DataHolder.instance.setCurrentUser(user)
        DataHolder.instance.setCurrentUserProfile(userProfile)

        //listener1.onResultReceived(true)
        listener1.onResultReceived(true)
    }

    fun fetchUser(userId: String, listener: FBQueryCompletedListener) {
        debugLog(TAG, "Fetch User : " + userId)

        val query = rdb.child(USERS_PATH).child(userId)
        query.keepSynced(true)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                debugLog(TAG,"Exist or not : " + dataSnapshot)
                debugLog(TAG,"Exist or not : " + dataSnapshot.exists() + " : " + dataSnapshot.childrenCount)

                if (dataSnapshot.exists()) {
                    // do something with the individual "user"
                    val user = dataSnapshot.getValue(AppUser::class.java)!!
                    user.userId = dataSnapshot.key.toString()
                    DataHolder.instance.setCurrentUser(user)

                    //Now fetch userprofile data
                    fetchUserProfile(userId, listener)

                    //Let's attach FCM Token too if available
                    //Pass it to Firebase database too
                    val token = DataHolder.instance.getFCMToken()
                    debugLog(TAG, "FCM token available : " + token)
                    if(token.isNotEmpty()) {
                        setFcmToken(token)
                    }
                } else {
                    listener.onResultReceived(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                debugLog(TAG,"onCancelled : " + databaseError.message)
                listener.onResultReceived(null)
            }
        })
    }

    fun fetchUserProfile(userId: String, listener: FBQueryCompletedListener) {

        val docRef = mFireStore.collection(USERS_STORE_PATH)
            .document(userId)


        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                //debugLog(TAG,"UserPRofile Exist or not : " + documentSnapshot)
                //debugLog(TAG,"UserPRofile Exist or not : " + documentSnapshot.exists())

                if (documentSnapshot.exists()) {
                    // do something with the individual "user"
                    val userProfile = documentSnapshot.toObject(UserProfile::class.java)!!
                    if(userId.equals(DataHolder.instance.getCurrentUserId())) {
                        DataHolder.instance.setCurrentUserProfile(userProfile)
                    }

                    listener.onResultReceived(userProfile)
                } else {
                    listener.onResultReceived(null)
                }
            }
            .addOnFailureListener {
                listener.onResultReceived(null)
            }
    }

    fun processInvitationCode(code: String) {
        debugLog(TAG, "processInvitationCode  : " + code)
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            debugLog(TAG,"User is NULL, Return")
            return
        }

        user.invitationCode = code

        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("invitationCode").setValue(code)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }


    fun updatePhoto(url: String) {
        debugLog(TAG,"updatePhoto  : " + url)
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            debugLog(TAG,"User is NULL, Return")
            return
        }

        user.photo = url
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("photo").setValue(url)

        val userProfile = DataHolder.instance.getCurrentUserProfile()
        userProfile!!.photo = url

        mFireStore.collection(USERS_STORE_PATH)
            .document(user.userId)
            .update("photo", url)
            .addOnSuccessListener { debugLog(TAG,"Completed store : SUCCESS") }
            .addOnFailureListener { e -> errorLog(TAG, "Error writing document" + e.toString()) }

        //set user in database
        DataHolder.instance.setCurrentUser(user)
        DataHolder.instance.setCurrentUserProfile(userProfile)

    }

    fun updateName(name: String) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.name = name
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("name").setValue(name)

        val userProfile = DataHolder.instance.getCurrentUserProfile()
        userProfile!!.name = name

        mFireStore.collection(USERS_STORE_PATH)
            .document(user.userId)
            .update("name", name)
            .addOnSuccessListener { debugLog(TAG, "Completed store : SUCCESS") }
            .addOnFailureListener { e -> errorLog(TAG, "Error writing document" + e.toString()) }

        //set user in database
        DataHolder.instance.setCurrentUser(user)
        DataHolder.instance.setCurrentUserProfile(userProfile)
    }

    fun updateState(state: String) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.state = state
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("state").setValue(state)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateVeteran(veteran: Veteran) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.veteranStatus = veteran
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("veteranStatus").setValue(veteran)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateProviderAttributes(attributes: TherapistProviderAttributes) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.providerAttributes = attributes
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("providerAttributes").setValue(attributes)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateContactTime(contactTime: ContactTime) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.bestTimeToContact = contactTime
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("bestTimeToContact").setValue(contactTime)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    /*fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        printLog("updatePaymentMethod  : " + paymentMethod)
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            printLog("User is NULL, Return")
            return
        }

        user.paymentMethod = paymentMethod
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("paymentMethod").setValue(paymentMethod)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    */fun updatePhone(phone: String) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.phone = phone
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("phone").setValue(phone)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateAge(selectedAge: Int) {
        debugLog(TAG, "updateAge  : " + selectedAge)
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.ageGroup = selectedAge
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("ageGroup").setValue(selectedAge)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateGender(selection: Int) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.gender = selection
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("gender").setValue(selection)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateGoal(goal: UserTopGoal) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.topGoal = goal
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("topGoal").setValue(goal)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateUserChallenges(selection: String) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.topChallenges = selection
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("topChallenges").setValue(selection)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateHealthProfessional(selection: Boolean) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.goingToTherapy = selection
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("goingToTherapy").setValue(selection)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateCBT(selection: Boolean) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.knowCbt = selection
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("knowCbt").setValue(selection)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateMakePromise(selection: Boolean) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.committedToSelfhelp = selection
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("committedToSelfhelp").setValue(selection)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun updateReminder(selection: Boolean) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.activatedReminderAtStartup = selection
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("activatedReminderAtStartup").setValue(selection)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun profileCompleted(selection: Boolean) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        user.profileCompleted = selection
        //set user in database
        rdb.child(USERS_PATH).child(user.userId).child("profileCompleted").setValue(selection)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun setFcmToken(token: String?) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        //set user in database
        rdb.child(USERS_PATH).child(user.userId)
            .child("fcmTokens")
            .child(DataHolder.instance.getDeviceId()).setValue(token)

        user.devicePlatform = AppUser.DevicePlatform.Android
        //also update the device platform
        rdb.child(USERS_PATH).child(user.userId)
            .child("devicePlatform").setValue(user.devicePlatform)

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun removeFcmToken() {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        //user.profileCompleted = selection
        //set user in database
        rdb.child(USERS_PATH).child(user.userId)
            .child("fcmTokens")
            .child(DataHolder.instance.getDeviceId()).removeValue()

    }


    fun setFreshChatRestoreId(id: String) {
        //get current user
        val user = DataHolder.instance.getCurrentUser()
        if(user == null) {
            errorLog(TAG, "User is NULL, Return")
            return
        }

        //set user in database
        rdb.child(USERS_PATH).child(user.userId)
            .child("freshChatRestoreID")
            .setValue(id)

        user.freshChatRestoreID = id

        //set user in database
        DataHolder.instance.setCurrentUser(user)
    }

    fun removeProfileListener(userid: String, listener: ValueEventListener) {
        rdb.child(USERS_PATH).child(userid).removeEventListener(listener)
    }

    //fun removeFcmToken()
}