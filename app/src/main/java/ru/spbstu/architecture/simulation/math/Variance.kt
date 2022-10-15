package ru.spbstu.architecture.simulation.math

import kotlin.math.pow

fun List<Double>.variance(): Double {
    val average = average()
    return sumOf { (it - average).pow(2) } / (size - 1)
}