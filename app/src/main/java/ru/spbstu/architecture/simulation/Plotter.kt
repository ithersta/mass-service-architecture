package ru.spbstu.architecture.simulation

class Plotter(private val config: Config) {
    fun simulate(): Result {
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