package com.health.mental.mooditude.data.model

import java.util.*

class CompanyInfo(
    var id: String = "",
    var name: String = "",
    var activeCode: String = "",
    var infoDesc: String? = null,
    var imgStr: String? = null,
    var brandImgStr: String? = null,
    var expiryDate: Date? = null,

    //var expiryDateStr: String { return expiryDate.toString(format: DateFormat.MDYSlash.rawValue) }


) {

}
