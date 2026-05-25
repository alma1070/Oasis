package com.almaslowcore.oasis.features.journal.presentation.state

sealed interface JournalEntryFormMode {

    data object Create : JournalEntryFormMode

    data class Edit(
        val entryId: Long
    ) : JournalEntryFormMode
}