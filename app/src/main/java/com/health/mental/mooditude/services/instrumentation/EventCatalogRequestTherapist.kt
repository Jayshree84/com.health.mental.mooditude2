package com.health.mental.mooditude.services.instrumentation

import com.health.mental.mooditude.data.entity.TherapistRequest
import com.health.mental.mooditude.data.model.ApiTherapistRequest

/**
 * Created by Jayshree Rathod on 02,October,2021
 */
fun EventCatalog.openedRequestTherapist() {
    event("openedRequestTherapist", mapOf(), false)
}

fun EventCatalog.therapistRequestStep(step: String) {
    event("therapistRequestStep_" + step, mapOf(), false)
}

fun EventCatalog.sendRequestTherapist(request: ApiTherapistRequest) {
    var reqId = "N/A"
    if (request.requestId != null) {
        reqId = request.requestId!!
    }
    val map = mapOf<String, Any>(
        Pair("paymentType", request.requestInfo.paymentMethod),
        Pair("state", request.requestInfo.state),
        Pair("veteranStatus", request.requestInfo.veteranStatus),
        Pair("requestId", reqId),
        Pair("assessmentScore", request.requestInfo.assessmentScore),
    )
    event("requestedTherapist", map, false)
}


fun EventCatalog.ratedTherapyRequest(rating: Int, noOneCalled: Boolean, requestId: String) {
    val map = mapOf<String, Any>(
        Pair("rating", rating),
        Pair("requestId", requestId),
    )
    event("ratedTherapyRequest", map, (rating > 3))

//        if noOneCalled {
//            event("noOneCalled", ["requestId": requestId
//            ], requestReview: false)
//        }
}