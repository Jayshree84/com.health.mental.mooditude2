package com.health.mental.mooditude.data.model.journal

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
data class JournalPromptStep(
    var title: String = "",
    var desc: String? = "",
    var isAttributedText: Boolean = false,
    var input: Boolean = false,
    var userInput: String? = null,
    var inputPlaceholder: String? = "",
    var order: Int = 0,
    var imgStr: String? = ""
    /*var image: URL? {
    if let imgStr = imgStr{
        return URL(string: imgStr)
    }*/
)
{

}
