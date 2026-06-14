package com.almaslowcore.oasis.features.activity.presentation.model

data class CreateActivityUiState(
    val formState: CreateActivityFormState = CreateActivityFormState(),
    val validationResult: CreateActivityValidationResult = CreateActivityValidationResult(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

data class CreateActivityValidationResult(
    val isValid: Boolean = false,
    val titleError: String? = null,
    val dueDateError: String? = null,
    val targetValueError: String? = null,
    val unitError: String? = null,
    val subtaskError: String? = null,
    val specificTimeError: String? = null,
    val repeatIntervalError: String? = null,
    val repeatUnitError: String? = null,
    val repeatStartDateError: String? = null,
    val repeatEndDateError: String? = null,
    val repeatEndOccurrencesError: String? = null
)