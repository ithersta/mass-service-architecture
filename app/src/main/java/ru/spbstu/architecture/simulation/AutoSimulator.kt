package ru.spbstu.architecture.simulation

class AutoSimulator(private val config: Config) {
    fun work(maxRequests: Int): Result {
        val defaultConfig = Simulator.Config(
            sourceCount = config.defaultSourceCount,
            deviceCount = config.defaultDeviceCount,
            bufferSize = config.defaultBufferSize,
            sourceIntensity = config.sourceIntensity,
            deviceProcessingTime = config.deviceProcessingTime
        )
        return Result(
            varyingSourcePlots = createPlots(maxRequests, config.sourceCountRange.asSequence()
                .map { it to defaultConfig.copy(sourceCount = it) }),
            varyingDevicePlots = createPlots(maxRequests, config.deviceCountRange.asSequence()
                .map { it to defaultConfig.copy(deviceCount = it) }),
            varyingBufferPlots = createPlots(maxRequests, config.bufferSizeRange.asSequence()
                .map { it to defaultConfig.copy(bufferSize = it) })
        )
    }

    private fun createPlots(
        maxRequests: Int,
        configs: Sequence<Pair<Int, Simulator.Config>>
    ): Result.Plots {
        val (averageUtilization, denyProbability, averageTimeSpent) = configs.map { (x, config) ->
            val simulator = Simulator(config)
            @Suppress("ControlFlowWithEmptyBody")
            while (simulator.step(maxRequests)) { }
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