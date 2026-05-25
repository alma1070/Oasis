package com.almaslowcore.oasis.features.journal.presentation.state

sealed interface JournalEntryFormEvent {

    data object NavigateBack : JournalEntryFormEvent

    data class ShowMessage(
        val message: String
    ) : JournalEntryFormEvent
}