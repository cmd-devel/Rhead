package com.cmd.rhead.repository

class SharableLoadException(message: String) : Exception(message)

interface Sharable {
    fun dump(): String
    fun load(encoded: String)
}