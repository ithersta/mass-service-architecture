package ru.spbstu.architecture.simulation.entities

sealed interface HasNextEvent {
    val nextEventTime: Double?
    fun onEvent(): Event
}