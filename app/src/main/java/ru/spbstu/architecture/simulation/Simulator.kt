package ru.spbstu.architecture.simulation

import ru.spbstu.architecture.simulation.parts.Buffer
import ru.spbstu.architecture.simulation.parts.Device
import ru.spbstu.architecture.simulation.parts.Source

class Simulator(private val config: Config) {
    private val sources = List(config.sourceCount) { Source(it, config.sourceIntensity) }
    private val devices = List(config.deviceCount) { Device(it, config.deviceProcessingTime) }
    private val buffer = Buffer(config.bufferSize)
    private val deniedRequests = mutableListOf<Request.Denied>()
    private val processedRequests = mutableListOf<Request.Processed>()
    private val stats = Stats()

    fun step() {
        val event = (sources.asSequence() + devices)
            .filter { it.nextEventTime != null }
            .minByOrNull { it.nextEventTime!! }
            ?.onEvent() ?: TODO()

        when (event) {
            is Event.RequestEmitted -> {
                stats.registerEmittedRequest(event.request)
                buffer.put(event.request)?.let {
                    stats.registerDeniedRequest(it)
                    deniedRequests.add(it)
                }
            }
            is Event.RequestProcessed -> {
                stats.registerProcessedRequest(event.request)
                processedRequests.add(event.request)
            }
        }

        while (true) {
            val freeDevice = devices.firstOrNull { it.isFree() } ?: break
            val waitingRequest = buffer.pop() ?: break
            val request = waitingRequest.beingProcessed(freeDevice.index, event.at)
            stats.registerBeingProcessedRequest(request)
            freeDevice.processRequest(request)
        }
    }

    fun createEventCalendar(): EventCalendar {
        return EventCalendar(
            sources = sources.map { source ->
                EventCalendar.SourceRow(
                    index = source.index,
                    time = stats.timeBySource[source.index],
                    requestCount = stats.emittedRequestCountBySource[source.index] ?: 0,
                    deniedRequestCount = stats.deniedRequestCountBySource[source.index] ?: 0
                )
            },
            devices = devices.map { device ->
                EventCalendar.DeviceRow(
                    index = device.index,
                    time = stats.timeByDevice[device.index],
                    isFree = device.isFree(),
                    requestCount = stats.processedRequestCountByDevice[device.index] ?: 0
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