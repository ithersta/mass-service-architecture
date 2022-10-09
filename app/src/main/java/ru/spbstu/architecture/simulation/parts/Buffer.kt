package ru.spbstu.architecture.simulation.parts

import ru.spbstu.architecture.simulation.Request
import ru.spbstu.architecture.simulation.denied

class Buffer(size: Int) {
    private var index = 0
    val array = Array<Request.Waiting?>(size) { null }

    fun put(request: Request.Waiting): Request.Denied? {
        val initialIndex = index
        while (array[index] != null) {
            index = (index + 1) % array.size
            if (initialIndex == index) break
        }
        return array[index]?.denied(request.producedAt).also { array[index] = request }
    }

    fun pop(): Request.Waiting? {
        return array.filterNotNull().minByOrNull { it.producedAt }
    }
}