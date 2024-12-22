package com.cmd.rhead.datasource

import com.cmd.rhead.model.Element
import com.cmd.rhead.model.ElementId

interface ElementDataSource {
    fun get(elementId: ElementId): Element?
    fun getAll(): Collection<Element>
    fun add(element: Element)
    fun addOrUpdate(element: Element)
    fun delete(elementId: ElementId)
}