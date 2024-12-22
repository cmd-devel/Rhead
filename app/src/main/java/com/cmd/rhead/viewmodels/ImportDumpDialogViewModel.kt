package com.cmd.rhead.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.rhead.repository.AbstractElementRepository
import com.cmd.rhead.repository.SharableLoadException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImportDumpDialogViewModel(encoded: String, elementRepository: AbstractElementRepository) :
    ViewModel() {
    private val isLoadingState = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = isLoadingState

    private val messageState = MutableStateFlow("Data imported successfully")
    val message: StateFlow<String> = messageState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                elementRepository.load(encoded)
            } catch (e: SharableLoadException) {
                messageState.value = e.message!!
            } finally {
                isLoadingState.value = false
            }
        }
    }
}