package ru.spbstu.architecture.simulation

import android.util.Log
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt

class AutoSimulator(private val config: Simulator.Config) {
    fun withPrecision(
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
            maxRequests = ceil(tAlpha.pow(2) * (1 - p) / (p * delta.pow(2))).roundToInt()
                .coerceAtMost(1000)
            if (abs(p - denyProbability) / denyProbability < delta || denyProbability < 0.001) {
                Log.d("SIMULATOR", maxRequests.toString())
                return simulator
            }
            denyProbability = p
        }
    }
}