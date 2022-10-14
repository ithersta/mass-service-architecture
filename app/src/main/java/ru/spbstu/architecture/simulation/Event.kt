package ru.spbstu.architecture.simulation

sealed interface Event {
    val at: Double

    data class RequestEmitted(val request: Request.Waiting, override val at: Double) : Event
    data class RequestProcessed(val request: Request.Processed, override val at: Double) : Event
}