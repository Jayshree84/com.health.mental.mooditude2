package com.health.mental.mooditude.data.model.journal

/**
 * Created by Jayshree Rathod on 06,July,2021
 */
data class ApiJournalPrompt(
    var id: String = "",
    var groupId: String = "",
    var title: String = "",
    var desc: String? = null,
    var isPremium: Boolean = false,
    var isActive: Boolean = true,
    var order: Int = 0,
    var steps:ArrayList<JournalPromptStep>? = null,
    var imgStr: String? = null,


    //var attachmentType: EntryAttachmentType = .guidedJournal
    //var updatedEmotion: EmotionType? = nil
    //var updatedEmotionIntensity: Int? = nil

    ) {




    }

