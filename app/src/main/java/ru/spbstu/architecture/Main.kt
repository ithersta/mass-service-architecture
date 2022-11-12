package ru.spbstu.architecture

import ru.spbstu.architecture.simulation.Bruteforcer
import java.io.File

fun main() {
    val csv = Bruteforcer().generateCsv()
    File("results2.csv").writeText(csv)
}