package com.cmd.rhead.model

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun ElementIdToString(elementId: ElementId): String {
        return elementId.toString()
    }

    @TypeConverter
    fun StringToElementId(string: String): ElementId {
        return ElementId(string)
    }
}