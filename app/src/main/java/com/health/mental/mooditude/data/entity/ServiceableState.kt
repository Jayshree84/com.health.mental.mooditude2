package com.health.mental.mooditude.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.health.mental.mooditude.data.model.ApiServiceableState

/**
 * Created by Jayshree Rathod on 07,July,2021
 */
@Entity(tableName = "ServiceableStates")
data class ServiceableState(
    @PrimaryKey
    var stateId: String) {
    var isActive: Boolean = true

    companion object {

        fun fromApiData(apiState: ApiServiceableState): ServiceableState {
            val state = ServiceableState(apiState.id)
            state.isActive = apiState.isActive
            return state
        }

        fun toApiData(state: ServiceableState): ApiServiceableState {
            val apiState = ApiServiceableState(state.stateId, state.isActive)
            return apiState
        }
    }

}
