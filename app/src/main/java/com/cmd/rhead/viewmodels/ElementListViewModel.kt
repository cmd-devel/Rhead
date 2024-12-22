package com.cmd.rhead.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.rhead.model.Element
import com.cmd.rhead.model.ElementId
import com.cmd.rhead.repository.AbstractElementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class ElementListSortMode(val sortModeName: String) {
    DATE("Date"),
    PRIORITY("Priority"),
}

class ElementListViewModel(
    private val elementRepository: AbstractElementRepository,
    private val onElementClickNav: (ElementId) -> Unit
) : ViewModel() {
    private val listState = MutableStateFlow(arrayListOf<Element>())
    var list: StateFlow<ArrayList<Element>> = this.listState

    private val sortModeState = MutableStateFlow(ElementListSortMode.DATE)
    var sortMode: StateFlow<ElementListSortMode> = this.sortModeState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            elementRepository.getElements().collect { elt ->
                listState.value = sortElements(elt)
            }
        }
    }

    private fun sortElements(list: Collection<Element>): ArrayList<Element> {
        val outList = when (sortMode.value) {
            ElementListSortMode.DATE -> list.sortedByDescending { it.timestamp }
            ElementListSortMode.PRIORITY -> list.sortedByDescending { it.priority }
        }

        return ArrayList(outList)
    }

    fun onSortModeChange(sortMode: ElementListSortMode) {
        sortModeState.value = sortMode
        listState.value = sortElements(listState.value)
    }

    fun onElementClick(index: Int) {
        if (index < listState.value.size) {
            onElementClickNav(listState.value[index].id)
        }
    }
}