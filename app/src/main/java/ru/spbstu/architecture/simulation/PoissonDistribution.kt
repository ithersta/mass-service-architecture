package ru.spbstu.architecture.simulation

import kotlin.math.ln
import kotlin.random.Random

fun poissonSequence(random: Random, intensity: Double): Sequence<Double> {
    return generateSequence { -ln(random.nextDouble()) / intensity }
        .runningReduce { acc, d -> acc + d }
}
