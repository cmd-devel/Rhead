package com.cmd.rhead.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.rhead.model.ElementId
import com.cmd.rhead.repository.AbstractElementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ElementDetailedViewViewModel(
    private val elementRepository: AbstractElementRepository,
    private val elementId: ElementId,
    private val onDismissRequest: () -> Unit
) : ViewModel() {
    private val nameState = MutableStateFlow("")
    private val locationState = MutableStateFlow("")
    private val deleteConfirmDialogVisibleState = MutableStateFlow(false)

    val name: StateFlow<String> = nameState
    val location: StateFlow<String> = locationState
    val deleteConfirmDialogVisible: StateFlow<Boolean> = deleteConfirmDialogVisibleState


    init {
        viewModelScope.launch(Dispatchers.IO) {
            elementRepository.getElement(elementId).collect { elt ->
                if (elt != null) {
                    nameState.value = elt.name
                    locationState.value = elt.location
                }
            }
        }
    }

    fun onDeleteConfirmation() {
        viewModelScope.launch(Dispatchers.IO) {
            elementRepository.deleteElement(elementId)
        }
        onDismissRequest()
    }

    fun onConfirmDialogCancelPressed() {
        deleteConfirmDialogVisibleState.value = false
    }

    fun onDeletePressed() {
        deleteConfirmDialogVisibleState.value = true
    }
}