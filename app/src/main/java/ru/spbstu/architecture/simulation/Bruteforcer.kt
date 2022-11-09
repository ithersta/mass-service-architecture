package ru.spbstu.architecture.simulation

import android.util.Log

class Bruteforcer {
    class DeviceType(
        val name: String,
        val processingTime: Pair<Double, Double>,
        val price: Int
    )

    class Row(
        val deviceTypeName: String,
        val deviceCount: Int,
        val bufferSize: Int,
        val price: Int,
        val denyProbability: Double,
        val averageUtilization: Double,
        val averageTimeSpent: Double
    )

    fun generateCsv(): String {
        val rows = mutableListOf<Row>()
        val bufferPrice = 2_000
        for (deviceCount in 1..20) {
            for (bufferSize in 1..20) {
                for (deviceType in listOf(
                    DeviceType("1", 0.5 to 0.75, 90_000),
                    DeviceType("2", 0.75 to 1.0, 50_000),
                    DeviceType("3", 1.0 to 1.25, 30_000)
                )) {
                    println(rows.size.toString())
                    val config = Simulator.Config(
                        sourceCount = 768,
                        deviceCount = deviceCount,
                        bufferSize = bufferSize,
                        sourceIntensity = 0.003,
                        deviceProcessingTime = deviceType.processingTime
                    )
                    val simulator = AutoSimulator(config).simulate(10_000)
                    rows.add(Row(
                        deviceTypeName = deviceType.name,
                        deviceCount = deviceCount,
                        bufferSize = bufferSize,
                        price = bufferPrice * bufferSize + deviceType.price * deviceCount,
                        denyProbability = simulator.calculateDenyProbability(),
                        averageUtilization = simulator.calculateAverageUtilization(),
                        averageTimeSpent = simulator.calculateAverageTimeSpent()
                    ))
                }
            }
        }
        return rows.joinToString(separator = "\n") {
            with(it) {
                "$deviceTypeName,$deviceCount,$bufferSize,$price,$denyProbability,$averageUtilization,$averageTimeSpent"
            }
        }
    }
}