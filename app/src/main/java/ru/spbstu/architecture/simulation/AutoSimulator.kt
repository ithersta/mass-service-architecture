package ru.spbstu.architecture.simulation

import android.util.Log
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt

class AutoSimulator(private val config: Config) {
    fun work(): Result {
        val defaultConfig = Simulator.Config(
            sourceCount = config.defaultSourceCount,
            deviceCount = config.defaultDeviceCount,
            bufferSize = config.defaultBufferSize,
            sourceIntensity = config.sourceIntensity,
            deviceProcessingTime = config.deviceProcessingTime
        )
        return Result(
            varyingSourcePlots = createPlots(config.sourceCountRange.asSequence()
                .map { it to defaultConfig.copy(sourceCount = it) }),
            varyingDevicePlots = createPlots(config.deviceCountRange.asSequence()
                .map { it to defaultConfig.copy(deviceCount = it) }),
            varyingBufferPlots = createPlots(config.bufferSizeRange.asSequence()
                .map { it to defaultConfig.copy(bufferSize = it) })
        )
    }

    private fun createPlots(
        configs: Sequence<Pair<Int, Simulator.Config>>
    ): Result.Plots {
        val (averageUtilization, denyProbability, averageTimeSpent) = configs.map { (x, config) ->
            val simulator = simulateWithPrecision(config)
            Triple(
                x to simulator.calculateAverageUtilization(),
                x to simulator.calculateDenyProbability(),
                x to simulator.calculateAverageTimeSpent()
            )
        }.asIterable().unzip()
        return Result.Plots(averageUtilization, denyProbability, averageTimeSpent)
    }

    private fun simulateWithPrecision(
        config: Simulator.Config,
        tAlpha: Double = 1.643,
        delta: Double = 0.1,
        initialMaxRequests: Int = 100
    ): Simulator {
        var maxRequests = initialMaxRequests
        var denyProbability = Double.NaN
        while (true) {
            val simulator = Simulator(config)
            @Suppress("ControlFlowWithEmptyBody")
            while (simulator.step(maxRequests)) {
            }
            val p = simulator.calculateDenyProbability()
            maxRequests = ceil(tAlpha.pow(2) * (1 - p) / (p * delta.pow(2))).roundToInt().coerceAtMost(1000)
            if (abs(p - denyProbability) / denyProbability < delta || denyProbability < 0.001) {
                Log.d("SIMULATOR", maxRequests.toString())
                return simulator
            }
            denyProbability = p
        }
    }

    data class Result(
        val varyingSourcePlots: Plots,
        val varyingDevicePlots: Plots,
        val varyingBufferPlots: Plots
    ) {
        data class Plots(
            val averageUtilization: List<Pair<Int, Double>>,
            val denyProbability: List<Pair<Int, Double>>,
            val averageTimeSpent: List<Pair<Int, Double>>
        )
    }

    data class Config(
        val defaultSourceCount: Int,
        val defaultDeviceCount: Int,
        val defaultBufferSize: Int,
        val sourceIntensity: Double,
        val deviceProcessingTime: Pair<Double, Double>,
        val sourceCountRange: IntRange,
        val deviceCountRange: IntRange,
        val bufferSizeRange: IntRange
    )
}