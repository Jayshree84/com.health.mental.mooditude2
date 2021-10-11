package com.health.mental.mooditude.data.model

enum class Religion {
    unknown, islam, christianity, judaism, hinduism, buddhism, other;

    public fun getLocalizedString(): String {
        when (this) {
            unknown -> {
                return "Unknown"
            }
            islam -> {
                return "Islam"
            }
            christianity -> {
                return "Christianity"
            }
            judaism -> {
                return "Judaism"
            }
            hinduism -> {
                return "Hinduism"
            }
            buddhism -> {
                return "Buddhism"
            }
            other -> {
                return "Other"
            }
        }
    }

}

