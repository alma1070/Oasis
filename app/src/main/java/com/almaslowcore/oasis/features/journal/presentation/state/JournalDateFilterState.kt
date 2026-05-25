package com.almaslowcore.oasis.features.journal.presentation.state

import java.time.LocalDate

data class JournalDateFilterState(
    val selectedDate: LocalDate = LocalDate.now(),
    val filterMode: JournalFilterMode = JournalFilterMode.DAY
)