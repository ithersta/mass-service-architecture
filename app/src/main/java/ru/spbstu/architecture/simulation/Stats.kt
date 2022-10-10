package ru.spbstu.architecture.simulation

class Stats {
    val emittedRequestCountBySource = mutableMapOf<Int, Int>()
    val deniedRequestCountBySource = mutableMapOf<Int, Int>()
    val processedRequestCountByDevice = mutableMapOf<Int, Int>()
    var emittedRequestCount = 0
        private set

    fun registerEmittedRequest(request: Request.Waiting) {
        emittedRequestCount += 1
        emittedRequestCountBySource.merge(request.sourceIndex, 1, Int::plus)
    }

    fun registerDeniedRequest(request: Request.Denied) {
        deniedRequestCountBySource.merge(request.sourceIndex, 1, Int::plus)
    }

    fun registerProcessedRequest(request: Request.Processed) {
        processedRequestCountByDevice.merge(request.deviceIndex, 1, Int::plus)
    }
}