package ru.spbstu.architecture.simulation

import kotlinx.serialization.Serializable
import ru.spbstu.architecture.simulation.entities.Buffer
import ru.spbstu.architecture.simulation.entities.Device
import ru.spbstu.architecture.simulation.entities.Event
import ru.spbstu.architecture.simulation.entities.Request
import ru.spbstu.architecture.simulation.entities.Source
import ru.spbstu.architecture.simulation.entities.beingProcessed
import ru.spbstu.architecture.simulation.math.variance

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

    fun createTable(): Table {
        val waitingTimes = (deniedRequests.asSequence() + processedRequests)
            .groupBy { it.sourceIndex }
            .mapValues { (_, value) ->
                value.map { it.leftBufferAt - it.producedAt }
            }
        val processingTimes = processedRequests
            .groupBy { it.sourceIndex }
            .mapValues { (_, value) ->
                value.map { it.processedAt - it.leftBufferAt }
            }
        val utilizationByDevice = processedRequests
            .groupBy { it.deviceIndex }
            .mapValues { (_, requests) ->
                requests.sumOf { it.processedAt - it.leftBufferAt } / lastTime
            }
        return Table(
            sources = sources.map { source ->
                Table.SourceRow(
                    index = source.index,
                    requestCount = source.emittedCount,
                    denyProbability = source.deniedCount.toDouble() / source.emittedCount,
                    averageWaitingTime = waitingTimes[source.index]?.average() ?: 0.0,
                    averageProcessingTime = processingTimes[source.index]?.average() ?: 0.0,
                    waitingTimeVariance = waitingTimes[source.index]?.variance() ?: 0.0,
                    processingTimeVariance = processingTimes[source.index]?.variance() ?: 0.0
                )
            },
            devices = devices.map { device ->
                Table.DeviceRow(
                    index = device.index,
                    utilization = utilizationByDevice[device.index] ?: 0.0
                )
            }
        )
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

    fun calculateAverageTimeSpent() = (processedRequests.map { it.processedAt - it.producedAt } +
            deniedRequests.map { it.leftBufferAt - it.producedAt }).average()

    fun calculateDenyProbability() = deniedRequests.size.toDouble() / emittedRequestCount

    fun calculateAverageUtilization() = processedRequests
        .groupBy { it.deviceIndex }
        .map { (_, requests) ->
            requests.sumOf { it.processedAt - it.leftBufferAt } / lastTime
        }
        .average()

    @Serializable
    data class Config(
        val sourceCount: Int,
        val deviceCount: Int,
        val bufferSize: Int,
        val sourceIntensity: Double,
        val deviceProcessingTime: Pair<Double, Double>
    )
}