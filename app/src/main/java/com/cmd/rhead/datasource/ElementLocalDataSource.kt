package com.cmd.rhead.datasource

import com.cmd.rhead.model.Element
import com.cmd.rhead.model.ElementDao
import com.cmd.rhead.model.ElementId

class ElementLocalDataSource(private val elementDao: ElementDao) : ElementDataSource {
    override fun get(elementId: ElementId): Element? {
        return elementDao.get(elementId)
    }

    override fun getAll(): Collection<Element> {
        return elementDao.getAll()
    }

    override fun add(element: Element) {
        elementDao.insert(element)
    }

    override fun addOrUpdate(element: Element) {
        elementDao.insertOrUpdate(element)
    }

    override fun delete(elementId: ElementId) {
        elementDao.delete(elementId)
    }
}