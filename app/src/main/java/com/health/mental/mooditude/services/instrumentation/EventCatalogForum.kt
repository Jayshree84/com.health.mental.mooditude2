package com.health.mental.mooditude.services.instrumentation

/**
 * Created by Jayshree Rathod on 01,October,2021
 */
fun EventCatalog.postCreated(postId: String, category: String, withImage: Boolean) {
    val map = mapOf<String, Any>(
        Pair("postId", postId),
        Pair("category", category),
        Pair("image", withImage),
    )
    event("postedOnForum", map, true)

    mMixPanelApi.people.increment("forumPosts", 1.0)
}


fun EventCatalog.interectedWithPost(postId: String, interactionType: String) {
    val map = mapOf<String, Any>(
        Pair("postId", postId),
        Pair("interactionType", interactionType),
    )
    event("interactedWithPost", map, true)
    mMixPanelApi.people.increment("forumInteractions", 1.0)
}


fun EventCatalog.filteredPosts(by: String) {
    val map = mapOf<String, Any>(
        Pair("filteredBy", by),
    )
    event("forumFiltered", map, true)
}

fun EventCatalog.sortedPosts(by: String) {
    val map = mapOf<String, Any>(
        Pair("sortedBy", by)
    )
    event("forumSorted", map, true)
}