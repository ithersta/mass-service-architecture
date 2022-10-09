package ru.spbstu.architecture.simulation

data class Config(
    val sourceCount: Int,
    val deviceCount: Int,
    val bufferSize: Int,
    val sourceIntensity: Double,
    val deviceProcessingTime: ClosedFloatingPointRange<Double>
)