package ru.spbstu.architecture.simulation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

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

    fun generateCsv(): String = runBlocking {
        val bufferPrice = 2_000
        val rows = buildList {
            for (deviceCount in 1..20) {
                for (bufferSize in 1..20) {
                    for (deviceType in listOf(
                        DeviceType("1", 0.5 to 0.75, 60_000),
                        DeviceType("2", 0.75 to 1.0, 50_000),
                        DeviceType("3", 1.0 to 1.25, 40_000)
                    )) {
                        add(async(Dispatchers.Default) {
                            val config = Simulator.Config(
                                sourceCount = 768,
                                deviceCount = deviceCount,
                                bufferSize = bufferSize,
                                sourceIntensity = 0.003,
                                deviceProcessingTime = deviceType.processingTime
                            )
                            val simulator = AutoSimulator(config).simulate(100_000)
                            Row(
                                deviceTypeName = deviceType.name,
                                deviceCount = deviceCount,
                                bufferSize = bufferSize,
                                price = bufferPrice * bufferSize + deviceType.price * deviceCount,
                                denyProbability = simulator.calculateDenyProbability(),
                                averageUtilization = simulator.calculateAverageUtilization(),
                                averageTimeSpent = simulator.calculateAverageTimeSpent()
                            )
                        })
                    }
                }
            }
        }.mapIndexed { index, it -> it.await().also { println(index) } }
        rows.joinToString(separator = "\n") {
            with(it) {
                "$deviceTypeName,$deviceCount,$bufferSize,$price," +
                        "\"${denyProbability.toString().replace(".", ",")}\"," +
                        "\"${averageUtilization.toString().replace(".", ",")}\"," +
                        "\"${averageTimeSpent.toString().replace(".", ",")}\""
            }
        }
    }
}