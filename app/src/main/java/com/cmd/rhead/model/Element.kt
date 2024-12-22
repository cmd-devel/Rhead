package com.cmd.rhead.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val ElementNameMaxLength = 50
val MinPriority = 1
val MaxPriority = 5

@Serializable
class ElementId() {
    @OptIn(ExperimentalUuidApi::class)
    private var uuid = Uuid.random().toString()

    constructor(uuidString: String) : this() {
        uuid = uuidString
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ElementId
        return uuid == other.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString(): String {
        return uuid
    }
}

@Entity
@Serializable
data class Element(
    @PrimaryKey val id: ElementId = ElementId(),
    val name: String,
    val location: String,
    val timestamp: Long,
    val priority: Int,
) {
    init {
        require(name.length <= ElementNameMaxLength)
        require(priority in MinPriority..MaxPriority)
    }
}

@Dao
interface ElementDao {
    @Insert
    fun insert(element: Element)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(element: Element)

    @Query("delete from element where id = :elementId")
    fun delete(elementId: ElementId)

    @Query("select * from element")
    fun getAll(): List<Element>

    @Query("select * from element where id = :elementId")
    fun get(elementId: ElementId): Element?
}