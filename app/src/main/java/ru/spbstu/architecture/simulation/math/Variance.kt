package ru.spbstu.architecture.simulation.math

import kotlin.math.pow

fun List<Double>.variance(): Double {
    if (size == 1) return 0.0
    val average = average()
    return sumOf { (it - average).pow(2) } / (size - 1)
}