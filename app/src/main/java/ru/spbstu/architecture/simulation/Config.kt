package ru.spbstu.architecture.simulation

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val sourceCount: Int,
    val deviceCount: Int,
    val bufferSize: Int,
    val sourceIntensity: Double,
    val deviceProcessingTime: Pair<Double, Double>
)