package ru.spbstu.architecture.simulation

import ru.spbstu.architecture.simulation.parts.Buffer
import ru.spbstu.architecture.simulation.parts.Device
import ru.spbstu.architecture.simulation.parts.Source

class Simulator(private val config: Config) {
    private val seed = config.hashCode()
    private val sources = List(config.sourceCount) { Source(it, config.sourceIntensity, seed) }
    private val devices = List(config.deviceCount) { Device(it, config.deviceProcessingTime, seed) }
    private val buffer = Buffer(config.bufferSize)
    private val deniedRequests = mutableListOf<Request.Denied>()
    private val processedRequests = mutableListOf<Request.Processed>()
    private var emittedRequestCount = 0
    private var lastTime: Double = 0.0

    fun step(maxRequests: Int): Boolean {
        if (emittedRequestCount >= maxRequests) {
            sources.forEach { it.pause() }
        }

        val event = (sources.asSequence() + devices)
            .filter { it.nextEventTime != null }
            .minByOrNull { it.nextEventTime!! }
            ?.onEvent() ?: return false
        lastTime = event.at

        when (event) {
            is Event.RequestEmitted -> {
                emittedRequestCount += 1
                buffer.put(event.request)?.let {
                    deniedRequests.add(it)
                }
            }
            is Event.RequestProcessed -> {
                processedRequests.add(event.request)
            }
        }

        while (true) {
            val freeDevice = devices.firstOrNull { it.isFree() } ?: break
            val waitingRequest = buffer.pop() ?: break
            val request = waitingRequest.beingProcessed(freeDevice.index, event.at)
            freeDevice.processRequest(request)
        }

        return true
    }

    fun createEventCalendar(): EventCalendar {
        return EventCalendar(
            sources = sources.map { source ->
                EventCalendar.SourceRow(
                    index = source.index,
                    time = source.nextEventTime,
                    requestCount = source.emittedCount,
                    deniedRequestCount = source.deniedCount
                )
            },
            devices = devices.map { device ->
                EventCalendar.DeviceRow(
                    index = device.index,
                    time = device.nextEventTime,
                    isFree = device.isFree(),
                    requestCount = device.processedCount
                )
            },
            buffer = buffer.array.mapIndexed { index, request ->
                EventCalendar.BufferRow(
                    index = index,
                    time = request?.producedAt,
                    sourceIndex = request?.sourceIndex
                )
            }
        )
    }
}