package ru.spbstu.architecture.ui.utils

fun String.toIntRangeOrNull() = runCatching {
    val (a, b) = split("..").map { it.toInt() }
    a..b
}.getOrNull()

fun String.toDoubleRangeOrNull() = runCatching {
    val (a, b) = split("..").map { it.toDouble() }
    a..b
}.getOrNull()

fun <T : Comparable<T>> ClosedFloatingPointRange<T>.toPair() = start to endInclusive