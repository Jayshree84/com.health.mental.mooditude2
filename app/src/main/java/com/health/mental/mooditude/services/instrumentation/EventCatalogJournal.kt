package com.health.mental.mooditude.services.instrumentation

import com.health.mental.mooditude.data.entity.Entry
import com.health.mental.mooditude.data.entity.JournalPrompt
import com.health.mental.mooditude.data.model.journal.EmotionType
import com.health.mental.mooditude.data.model.journal.EntryType
import org.json.JSONObject

/**
 * Created by Jayshree Rathod on 01,October,2021
 */
fun EventCatalog.createdEntryEvent(entry: Entry){

    var mood = "Unknown"
    if(entry.emotion != null) {
        mood = entry.emotion!!.name
    }
    val selectedActivities = entry.getUserActivityTitles()
    val postLength = entry.getPostLength()

    when(entry.entryType) {
        EntryType.mood-> {
            val map = mapOf<String, Any>(
                Pair("mood", mood),
                Pair("activities", selectedActivities),
            )
            event("loggedMood", map, true)

            for(activity in entry.activities) {
                val map2 = mapOf<String, Any>(
                    Pair("id", activity.activityId),
                    Pair("activityName", activity.title),
                )
                event("selectedActivity", map2)

                mMixPanelApi.registerSuperProperties(JSONObject(mapOf(Pair("mood", mood))))
                mMixPanelApi.people.increment("loggedMood", 1.0)
            }
        }
        EntryType.journal, EntryType.guidedJournal -> {

            var promptId = "free"
            if(entry.userInfo != null) {
                val prompt = JournalPrompt.getPromptFromUserInfo(entry.userInfo!!)
                promptId = prompt.id
            }
            val map = mapOf<String, Any>(
                Pair("journalType", entry.entryType.name),
                Pair("journalSubType", entry.attachmentType.name),
                Pair("promptId", promptId),
                Pair("image", entry.hasImage()),
                Pair("postLength", postLength),
            )
            event("journaled", map, true)
        }

        /*
        case .cbt:
        event("completedCbtExercise", ["cbtExerciseId" : entry.attachmentData?.attachmentType.rawValue ?? "Unknown",
        "imporovements": entry.improvements ?? 0],
        requestReview: true)

        event("loggedMood", ["mood": mood, "activities": selectedActivities])

        event("journaled", [ "journalType": entry.entryType.rawValue,
        "journalSubType": entry.attachmentType.rawValue,
        "promptId": entry.guidedJournal?.id ?? "free",
        "image": entry.hasImage,
        "postLength": postLength])

        for a in entry.activities  {
            event("selectedActivity", ["activityName": a.englishTitle])
        }

        for f in entry.feelings {
            event("selectedFeeling", ["type": f.group.rawValue])
        }

        if let tt = entry.attachmentData as? CbtThinkingError {
            for t in tt.identifiedErrors {
                event("topThinkingTraps", ["trapName": t.rawValue])
            }
        }

        mixpanel.people.increment(property: "completedCbt", by: 1.0)

        case .course:
        event("completedCourseExercise", ["course": entry.courseInfo?.courseId ?? "Unknown"], requestReview: true)
        event("journaled", [ "journalType": entry.entryType.rawValue,
        "journalSubType": entry.attachmentType.rawValue,
        "promptId": entry.courseInfo?.exerciseTitle ?? "Unknown",
        "postLength": postLength])
         */
    }


    if (entry.entryType != EntryType.mood) {
        mMixPanelApi.people.increment("journaled", 1.0)
    }

}

fun EventCatalog.createdNewActivity(activityTitle: String){
    val map = mapOf<String, Any>(
        Pair("activityName", activityTitle),
    )
    event("createdNewActivity", map)
}

fun EventCatalog.viewedJournalEntry(entry: Entry){
    val map = mapOf<String, Any>(
        Pair("journalType", entry.entryType.name),
        Pair("journalSubType", entry.attachmentType.name),
    )
    event("viewedJournalEntry", map, true)
}

fun EventCatalog.exportedEntries(){
    event("exportedJournalEntries", mapOf())
}

fun EventCatalog.deletedJournalEntry(entry: Entry){
    val map = mapOf<String, Any>(
        Pair("journalType", entry.entryType.name),
        Pair("journalSubType", entry.attachmentType.name),
    )
    event("deletedJournalEntry", map, true)
}

fun EventCatalog.editedJournalEntry(entry: Entry){
    val map = mapOf<String, Any>(
        Pair("journalType", entry.entryType.name),
        Pair("journalSubType", entry.attachmentType.name),
    )
    event("editedJournalEntry", map, true)
}


fun EventCatalog.viewedPromptCategoryDescription(category: String){
    val map = mapOf<String, Any>(
        Pair("promptCategory", category),
    )
    event("viewedPromptCategory", map, true)
}

fun EventCatalog.startedGuidedJournaling(prompt: JournalPrompt, stepCompletedInPercentage: Double){
    val map = mapOf<String, Any>(
        Pair("promptCategory", prompt.groupId),
        Pair("promptId", prompt.promptId),
        Pair("completion", (stepCompletedInPercentage * 100).toInt()),
    )
    event("startedGuidedJournaling", map, true)
}

fun EventCatalog. copingActivityUsed(activityId: String, mood: EmotionType?){
    var moodName = "Unknown"
    if(mood != null) {
        moodName = mood.name
    }
    val map = mapOf<String, Any>(
        Pair("mood", moodName),
        Pair("copingActivityId", activityId),
    )
    event("copingActivityUsed",map, true)
}
    