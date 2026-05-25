package com.almaslowcore.oasis.features.journal.domain.model

enum class MoodType(
    val score: Int,
    val defaultLabel: String,
    val emoji: String
) {
    VERY_BAD(
        score = 1,
        defaultLabel = "Very bad",
        emoji = "😞"
    ),

    BAD(
        score = 2,
        defaultLabel = "Bad",
        emoji = "😕"
    ),

    NEUTRAL(
        score = 3,
        defaultLabel = "Neutral",
        emoji = "😐"
    ),

    GOOD(
        score = 4,
        defaultLabel = "Good",
        emoji = "🙂"
    ),

    VERY_GOOD(
        score = 5,
        defaultLabel = "Very good",
        emoji = "😊"
    )
}