package com.cmd.rhead.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.rhead.model.Element
import com.cmd.rhead.model.ElementNameMaxLength
import com.cmd.rhead.model.MaxPriority
import com.cmd.rhead.model.MinPriority
import com.cmd.rhead.repository.AbstractElementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import java.util.Date

class NewElementViewModel(
    private val elementRepository: AbstractElementRepository,
    private val initalValues: NewElementInitialValues = NewElementInitialValues()
) : ViewModel() {
    // Initialized through the reset method
    private val nameState = MutableStateFlow("")
    private val locationState = MutableStateFlow("")
    private val priorityState = MutableStateFlow(MinPriority)
    private val canValidateState = MutableStateFlow(false)
    private val errorMessageState: MutableStateFlow<String?> = MutableStateFlow(null)

    val name: StateFlow<String> = nameState
    val location: StateFlow<String> = locationState
    val priority: StateFlow<Int> = priorityState
    val canValidate: StateFlow<Boolean> = canValidateState
    val errorMessage: StateFlow<String?> = errorMessageState

    init {
        this.reset()
        viewModelScope.launch {
            merge(nameState, locationState).collect {
                canValidateState.value =
                    nameState.value.isNotEmpty() && locationState.value.isNotEmpty() && nameState.value.length <= ElementNameMaxLength
            }
        }
    }

    fun onNameChange(newName: String) {
        if (newName.length <= ElementNameMaxLength) {
            nameState.value = newName
        }
    }

    fun onLocationChange(newLocation: String) {
        locationState.value = newLocation
    }

    fun onPriorityChange(newPriority: Int) {
        priorityState.value = newPriority.coerceIn(MinPriority..MaxPriority)
    }

    fun onErrorAcknowledged() {
        errorMessageState.value = null
    }

    fun validate(onCompleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                elementRepository.addElement(
                    Element(
                        name = nameState.value,
                        location = location.value,
                        timestamp = Date().time,
                        priority = priorityState.value
                    )
                )
                onCompleted()
            } catch (e: IllegalArgumentException) {
                errorMessageState.value = "Invalid arguments, element could not be created";
            }
        }
    }

    fun reset() {
        nameState.value = initalValues.name
        locationState.value = initalValues.location
        priorityState.value = MinPriority
        canValidateState.value = false
        errorMessageState.value = null
    }
}

data class NewElementInitialValues(
    val name: String = "",
    val location: String = "",
)