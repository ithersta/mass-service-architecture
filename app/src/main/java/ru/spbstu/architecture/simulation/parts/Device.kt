package ru.spbstu.architecture.simulation.parts

import ru.spbstu.architecture.simulation.Event
import ru.spbstu.architecture.simulation.Request
import ru.spbstu.architecture.simulation.beingProcessed
import ru.spbstu.architecture.simulation.processed
import kotlin.random.Random

class Device(val index: Int, private val processingTime: ClosedFloatingPointRange<Double>) : HasNextEvent {
    private val random = Random(seed = index)
    var request: Request.BeingProcessed? = null
        private set
    override var nextEventTime: Double? = null
        private set

    fun isFree() = request == null

    fun processRequest(request: Request.BeingProcessed) {
        assert(isFree())
        this.request = request
        nextEventTime = request.leftBufferAt + random.nextDouble(
            from = processingTime.start,
            until = processingTime.endInclusive
        )
    }

    override fun onEvent(): Event.RequestProcessed {
        assert(!isFree())
        return Event.RequestProcessed(request!!.processed(nextEventTime!!), nextEventTime!!).also {
            nextEventTime = null
            request = null
        }
    }
}