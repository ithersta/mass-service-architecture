package ru.spbstu.architecture.simulation.parts

import ru.spbstu.architecture.simulation.Event
import ru.spbstu.architecture.simulation.Request
import ru.spbstu.architecture.simulation.poissonSequence
import kotlin.random.Random

class Source(
    val index: Int,
    intensity: Double
) : HasNextEvent {
    private val poissonIterator = poissonSequence(Random(seed = index), intensity).iterator().withIndex()
    private var nextEvent = poissonIterator.next()
    override val nextEventTime: Double
        get() = nextEvent.value

    override fun onEvent(): Event.RequestEmitted {
        val request = Request.Waiting(sourceIndex = index, producedAt = nextEventTime)
        return Event.RequestEmitted(request, nextEventTime).also {
            nextEvent = poissonIterator.next()
        }
    }
}