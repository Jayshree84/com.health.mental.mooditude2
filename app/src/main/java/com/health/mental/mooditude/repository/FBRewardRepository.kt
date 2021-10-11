package com.health.mental.mooditude.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.health.mental.mooditude.data.AppDatabase
import com.health.mental.mooditude.data.entity.Reward
import com.health.mental.mooditude.data.model.ApiReward
import com.health.mental.mooditude.data.model.RewardType
import com.health.mental.mooditude.debugLog
import com.health.mental.mooditude.errorLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
class FBRewardRepository(private val mAppDb: AppDatabase,
                         private val rdb: DatabaseReference,
                         private val mFireStore: FirebaseFirestore
) {

    private val TAG = this.javaClass.simpleName
    private val REWARDS_PATH = "rewards/%s/%s"

    fun getRewards(type: RewardType, language: String) {
        val path = String.format(REWARDS_PATH, type.toString().lowercase(), language)
        val query1 = rdb.child(path)
        //query1.keepSynced(true)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                errorLog(TAG, "error : " + p0.code + " : " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val map = p0.value as HashMap<String, *>
                    val list = ArrayList<ApiReward>()
                    val list2 = ArrayList<Reward>()
                    for (key in map.keys) {
                        val json = Gson().toJson(map.get(key))
                        val reward =
                            Gson().fromJson(json, ApiReward::class.java)
                        //check for id
                        //reward.rewardId = key
                        list.add(reward)

                        //Now save this data to db
                        val rewardRecord = Reward.fromApiData(reward)
                        list2.add(rewardRecord)
                    }

                    //Call the listener to save data
                    saveToLocal(list2)
                }
            }
        })
    }


    private fun saveToLocal(list: ArrayList<Reward>) {
        CoroutineScope(Dispatchers.IO).launch {
            mAppDb.rewardDao().insertAll(list)
            debugLog(TAG, "onRewardReceived :: INSERTED : " + list.size)
        }
    }
}