package ru.spbstu.architecture.simulation

data class EventCalendar(
    val sources: List<SourceRow>,
    val devices: List<DeviceRow>,
    val buffer: List<BufferRow>
) {
    data class SourceRow(
        val index: Int,
        val time: Double?,
        val requestCount: Int,
        val deniedRequestCount: Int
    )

    data class DeviceRow(
        val index: Int,
        val time: Double?,
        val isFree: Boolean,
        val requestCount: Int
    )

    data class BufferRow(val index: Int, val time: Double?, val sourceIndex: Int?)
}