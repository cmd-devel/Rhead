package com.cmd.rhead.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cmd.rhead.model.Converters
import com.cmd.rhead.model.Element
import com.cmd.rhead.model.ElementDao

@Database(entities = [Element::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun elementDao(): ElementDao
}