package com.cmd.rhead.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ImportType {
    SINGLE_ELEMENT,
    DUMP,
}

class ImportViewModel : ViewModel() {
    private val showImportTypeDialogState = MutableStateFlow(true)
    val showImportTypeDialog: StateFlow<Boolean> = showImportTypeDialogState

    private val importTypeState = MutableStateFlow(ImportType.SINGLE_ELEMENT)
    val importType: StateFlow<ImportType> = importTypeState

    fun onImportTypeSelected(importType: ImportType) {
        importTypeState.value = importType
    }

    fun onValidate() {
        showImportTypeDialogState.value = false
    }
}