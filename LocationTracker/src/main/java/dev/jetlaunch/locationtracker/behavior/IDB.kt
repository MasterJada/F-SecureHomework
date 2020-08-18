package dev.jetlaunch.locationtracker.behavior

internal interface IDB<T> {
    fun readRecord(): T
    fun writeRecord(items: T): Boolean
}