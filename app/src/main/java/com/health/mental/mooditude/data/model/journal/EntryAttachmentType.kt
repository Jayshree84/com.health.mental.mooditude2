package com.health.mental.mooditude.data.model.journal

/**
 * Created by Jayshree Rathod on 17,August,2021
 */
enum class EntryAttachmentType {
    none,
    journal,
    guidedJournal,
    courseExercise,
    lookback,
    thoughts,
    thinkingErrors,
    evidence,
    beSpecific,
    affirmativeMeditation,
    acceptance,
    mirrorTalk;

    fun isCBT(): Boolean {
        if (this == none || this == guidedJournal || this == courseExercise || this == journal) {
            return false
        }

        return true
    }

}