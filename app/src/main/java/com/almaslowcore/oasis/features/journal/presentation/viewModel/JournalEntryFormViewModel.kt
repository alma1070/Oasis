package com.almaslowcore.oasis.features.journal.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.core.navigation.OasisDestination
import com.almaslowcore.oasis.features.journal.domain.model.JournalEntry
import com.almaslowcore.oasis.features.journal.domain.model.MoodType
import com.almaslowcore.oasis.features.journal.domain.repository.JournalRepository
import com.almaslowcore.oasis.features.journal.presentation.state.JournalEntryFormEvent
import com.almaslowcore.oasis.features.journal.presentation.state.JournalEntryFormMode
import com.almaslowcore.oasis.features.journal.presentation.state.JournalFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class JournalEntryFormViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val entryId: Long? =
        savedStateHandle.get<Long>(OasisDestination.JournalEdit.ENTRY_ID_ARG)

    val mode: JournalEntryFormMode =
        entryId?.let { JournalEntryFormMode.Edit(it) }
            ?: JournalEntryFormMode.Create

    private val _formState = MutableStateFlow(JournalFormState())
    val formState: StateFlow<JournalFormState> = _formState.asStateFlow()

    private val _event = Channel<JournalEntryFormEvent>()
    val event = _event.receiveAsFlow()

    private var originalEntry: JournalEntry? = null

    init {
        if (mode is JournalEntryFormMode.Edit) {
            loadEntryForEdit(mode.entryId)
        }
    }

    fun onDateChange(date: LocalDate) {
        _formState.update {
            it.copy(
                selectedDate = date
            )
        }
    }

    fun onTimeChange(time: LocalTime) {
        _formState.update {
            it.copy(selectedTime = time)
        }
    }

    fun onMoodSelected(mood: MoodType) {
        _formState.update {
            it.copy(selectedMood = mood)
        }
    }

    fun onNoteChange(note: String) {
        _formState.update {
            it.copy(
                note = note,
                noteError = null
            )
        }
    }

    fun onCancelClick() {
        viewModelScope.launch {
            _event.send(JournalEntryFormEvent.NavigateBack)
        }
    }

    fun onSaveClick() {
        val state = _formState.value

        /**
         * MVP decision:
         * Note is optional, so mood-only check-in is allowed.
         *
         * If later you want journal content to be required,
         * validate note here.
         */

        viewModelScope.launch {
            _formState.update {
                it.copy(isSaving = true)
            }

            runCatching {
                when (mode) {
                    JournalEntryFormMode.Create -> {
                        createEntry(state)
                    }

                    is JournalEntryFormMode.Edit -> {
                        updateEntry(state)
                    }
                }
            }.onSuccess {
                _formState.update {
                    it.copy(isSaving = false)
                }

                _event.send(JournalEntryFormEvent.NavigateBack)
            }.onFailure { throwable ->
                _formState.update {
                    it.copy(
                        isSaving = false,
                        noteError = throwable.message ?: "Could not save check-in"
                    )
                }

                _event.send(
                    JournalEntryFormEvent.ShowMessage(
                        message = throwable.message ?: "Could not save check-in"
                    )
                )
            }
        }
    }

    private fun loadEntryForEdit(entryId: Long) {
        viewModelScope.launch {
            val entry = journalRepository.getEntryById(entryId)

            if (entry == null) {
                _event.send(
                    JournalEntryFormEvent.ShowMessage(
                        message = "Check-in not found"
                    )
                )
                _event.send(JournalEntryFormEvent.NavigateBack)
                return@launch
            }

            originalEntry = entry

            val localDateTime = Instant
                .ofEpochMilli(entry.dateTime)
                .atZone(zoneId)
                .toLocalDateTime()

            _formState.update {
                it.copy(
                    selectedDate = localDateTime.toLocalDate(),
                    selectedTime = localDateTime.toLocalTime(),
                    selectedMood = entry.moodType,
                    note = entry.note,
                    noteError = null,
                    isSaving = false
                )
            }
        }
    }

    private suspend fun createEntry(
        state: JournalFormState
    ) {
        journalRepository.createEntry(
            moodType = state.selectedMood,
            note = state.note.trim(),
            dateTime = state.toDateTimeMillis(),
            relatedActivityId = null
        )
    }

    private suspend fun updateEntry(
        state: JournalFormState
    ) {
        val entryToUpdate = originalEntry ?: return

        journalRepository.updateEntry(
            entryToUpdate.copy(
                moodType = state.selectedMood,
                note = state.note.trim(),
                dateTime = state.toDateTimeMillis(),
                relatedActivityId = entryToUpdate.relatedActivityId
            )
        )
    }

    private fun JournalFormState.toDateTimeMillis(): Long {
        return selectedDate
            .atTime(selectedTime)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
    }

}