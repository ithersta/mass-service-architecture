package ru.spbstu.architecture.simulation.parts

import ru.spbstu.architecture.simulation.Event

sealed interface HasNextEvent {
    val nextEventTime: Double?
    fun onEvent(): Event
}