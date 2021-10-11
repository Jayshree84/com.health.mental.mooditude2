package com.health.mental.mooditude.data.model.journal

/**
 * Created by Jayshree Rathod on 17,August,2021
 */
class JournalPromptInfo {

    var type: String = ""
    var title: String = ""

    var attachmentType: EntryAttachmentType = EntryAttachmentType.journal
    var updatedEmotion: EmotionType? = null
    var updatedEmotionIntensity: Int? = 0

    enum class CodingKeys{
        type, title
    }

    fun init(type: String, title: String){
        this.type = type
        this.title = title
    }


}