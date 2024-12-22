package com.cmd.rhead.repository

import com.cmd.rhead.datasource.ElementDataSource
import com.cmd.rhead.model.Element
import com.cmd.rhead.model.ElementId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ElementRepository(private val dataSource: ElementDataSource) : AbstractElementRepository {
    private val updateElementsNotification =
        MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

    override fun getElements(): Flow<Collection<Element>> {
        return flow {
            emit(dataSource.getAll())
            updateElementsNotification.collect {
                emit(dataSource.getAll())
            }
        }
    }

    override fun getElement(elementId: ElementId): Flow<Element?> {
        return flow {
            emit(dataSource.get(elementId))
            updateElementsNotification.collect {
                emit(dataSource.get(elementId))
            }
        }
    }

    override fun addElement(element: Element) {
        dataSource.add(element)
        updateElementsNotification.tryEmit(Unit)
    }

    override fun deleteElement(elementId: ElementId) {
        dataSource.delete(elementId)
        updateElementsNotification.tryEmit(Unit)
    }

    // Refresh the list without waiting for the next update (useful when restarting from background)
    override fun refresh() {
        updateElementsNotification.tryEmit(Unit)
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun dump(): String {
        val elements = dataSource.getAll()
        val json = Json.encodeToString(elements)
        return Base64.Default.encode(json.encodeToByteArray())
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun load(encoded: String) {
        try {
            val json = Base64.Default.decode(encoded).decodeToString()
            val elements = Json.decodeFromString<Collection<Element>>(json)
            elements.forEach {
                dataSource.addOrUpdate(it)
            }
        } catch (e: Exception) {
            throw SharableLoadException(e.message ?: "Import failed for unknown reason")
        }
    }
}