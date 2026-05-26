package com.almaslowcore.oasis.features.journal.presentation.state

import com.almaslowcore.oasis.features.journal.domain.model.MoodType
import java.time.LocalDate
import java.time.LocalTime

data class JournalFormState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),

    val selectedMood: MoodType = MoodType.NEUTRAL,

    val note: String = "",
    val noteError: String? = null,

    val isSaving: Boolean = false
)