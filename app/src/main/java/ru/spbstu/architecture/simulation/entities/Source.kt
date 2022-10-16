package ru.spbstu.architecture.simulation.entities

import ru.spbstu.architecture.simulation.poissonSequence
import kotlin.random.Random

class Source(
    val index: Int,
    intensity: Double,
    seed: Int
) : HasNextEvent {
    private val poissonIterator =
        poissonSequence(Random(seed = index + seed), intensity).iterator().withIndex()
    private var nextEvent: IndexedValue<Double>? = poissonIterator.next()
    override val nextEventTime: Double?
        get() = nextEvent?.value
    var emittedCount = 0
        private set
    var deniedCount = 0
        private set

    override fun onEvent(): Event.RequestEmitted {
        val request = Request.Waiting(
            sourceIndex = index,
            producedAt = nextEventTime!!,
            onDeny = { deniedCount += 1 }
        )
        return Event.RequestEmitted(request, nextEventTime!!).also {
            nextEvent = poissonIterator.next()
            emittedCount += 1
        }
    }
}