package ru.spbstu.architecture.simulation

import android.util.Log
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt

class AutoSimulator(private val config: Simulator.Config) {
    fun simulateWithPrecision(
        tAlpha: Double = 1.643,
        delta: Double = 0.1,
        initialRequestCount: Int = 100,
        maxRequestCount: Int = 2000
    ): Simulator {
        var requestCount = initialRequestCount
        var denyProbability = Double.NaN
        while (true) {
            val simulator = Simulator(config, requestCount)
            @Suppress("ControlFlowWithEmptyBody")
            while (simulator.step()) {
            }
            val p = simulator.calculateDenyProbability()
            if (
                abs(p - denyProbability) / denyProbability < delta ||
                requestCount >= maxRequestCount
            ) {
                Log.d("SIMULATOR", requestCount.toString())
                return simulator
            }
            denyProbability = p
            requestCount = ceil(tAlpha.pow(2) * (1 - p) / (p * delta.pow(2))).roundToInt()
                .coerceIn(requestCount + 1, maxRequestCount)
        }
    }
}