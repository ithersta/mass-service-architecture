package ru.spbstu.architecture.simulation

import kotlinx.serialization.Serializable
import ru.spbstu.architecture.ui.utils.IntRangeSerializer

class Plotter(private val config: Config) {
    fun simulate(): Result {
        return Result(
            varyingSourcePlots = createPlots(config.sourceCountRange.asSequence()
                .map { it to config.defaultSimulatorConfig.copy(sourceCount = it) }),
            varyingDevicePlots = createPlots(config.deviceCountRange.asSequence()
                .map { it to config.defaultSimulatorConfig.copy(deviceCount = it) }),
            varyingBufferPlots = createPlots(config.bufferSizeRange.asSequence()
                .map { it to config.defaultSimulatorConfig.copy(bufferSize = it) })
        )
    }

    private fun createPlots(
        configs: Sequence<Pair<Int, Simulator.Config>>
    ): Result.Plots {
        val (averageUtilization, denyProbability, averageTimeSpent) = configs.map { (x, config) ->
            val simulator = AutoSimulator(config).withPrecision()
            Triple(
                x to simulator.calculateAverageUtilization(),
                x to simulator.calculateDenyProbability(),
                x to simulator.calculateAverageTimeSpent()
            )
        }.asIterable().unzip()
        return Result.Plots(averageUtilization, denyProbability, averageTimeSpent)
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

    @Serializable
    data class Config(
        val defaultSimulatorConfig: Simulator.Config,
        @Serializable(with = IntRangeSerializer::class) val sourceCountRange: IntRange,
        @Serializable(with = IntRangeSerializer::class) val deviceCountRange: IntRange,
        @Serializable(with = IntRangeSerializer::class) val bufferSizeRange: IntRange
    )
}