package com.cmd.rhead

import android.content.Context
import androidx.room.Room
import com.cmd.rhead.datasource.AppDatabase

fun getAppDatabase(applicationContext: Context): AppDatabase {
    return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app.db").build()
}