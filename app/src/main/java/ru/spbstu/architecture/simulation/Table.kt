package ru.spbstu.architecture.simulation

data class Table(
    val sources: List<SourceRow>,
    val devices: List<DeviceRow>
) {
    data class SourceRow(
        val index: Int,
        val requestCount: Int,
        val denyProbability: Double,
        val averageWaitingTime: Double?,
        val averageProcessingTime: Double?,
        val waitingTimeVariance: Double?,
        val processingTimeVariance: Double?
    ) {
        val averageTimeSpent get() = averageWaitingTime?.plus(averageProcessingTime ?: 0.0)
    }

    data class DeviceRow(
        val index: Int,
        val utilization: Double
    )
}