package ru.spbstu.architecture.simulation

class Stats {
    val emittedRequestCountBySource = mutableMapOf<Int, Int>()
    val deniedRequestCountBySource = mutableMapOf<Int, Int>()
    val processedRequestCountByDevice = mutableMapOf<Int, Int>()
    val timeBySource = mutableMapOf<Int, Double>()
    val timeByDevice = mutableMapOf<Int, Double>()
    var emittedRequestCount = 0
        private set

    fun registerEmittedRequest(request: Request.Waiting) {
        emittedRequestCount += 1
        emittedRequestCountBySource.merge(request.sourceIndex, 1, Int::plus)
        timeBySource[request.sourceIndex] = request.producedAt
    }

    fun registerDeniedRequest(request: Request.Denied) {
        deniedRequestCountBySource.merge(request.sourceIndex, 1, Int::plus)
    }

    fun registerBeingProcessedRequest(request: Request.BeingProcessed) {
        timeByDevice[request.deviceIndex] = request.leftBufferAt
    }

    fun registerProcessedRequest(request: Request.Processed) {
        processedRequestCountByDevice.merge(request.deviceIndex, 1, Int::plus)
        timeByDevice[request.deviceIndex] = request.processedAt
    }
}