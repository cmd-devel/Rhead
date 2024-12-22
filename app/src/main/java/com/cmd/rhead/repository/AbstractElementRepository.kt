package com.cmd.rhead.repository

import com.cmd.rhead.model.Element
import com.cmd.rhead.model.ElementId
import kotlinx.coroutines.flow.Flow

interface AbstractElementRepository : Sharable {
    fun getElements(): Flow<Collection<Element>>
    fun getElement(elementId: ElementId): Flow<Element?>
    fun addElement(element: Element)
    fun deleteElement(elementId: ElementId)
    fun refresh()
}