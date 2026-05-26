package com.almaslowcore.oasis.features.planning.presentation.domain.model

enum class LifeArea(val displayName: String) {
    PERSONAL_GROWTH("Personal Growth"),
    HEALTH("Health"),
    CAREER("Career"),
    MIND("Mind"),
    ENVIRONMENT("Environment");

    companion object {
        fun fromString(name: String?): LifeArea? {
            return entries.find { it.name == name || it.displayName == name }
        }
    }
}