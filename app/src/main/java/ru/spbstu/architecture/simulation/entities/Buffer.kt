package ru.spbstu.architecture.simulation.entities

class Buffer(size: Int) {
    private var index = 0
    val array = Array<Request.Waiting?>(size) { null }

    fun put(request: Request.Waiting): Request.Denied? {
        val initialIndex = index
        while (array[index] != null) {
            incrementIndex()
            if (initialIndex == index) break
        }
        return array[index]?.denied(request.producedAt).also {
            array[index] = request
            incrementIndex()
        }
    }

    fun pop(): Request.Waiting? {
        return array.withIndex()
            .filter { it.value != null }
            .minByOrNull { it.value!!.producedAt }
            ?.let { (index, value) ->
                array[index] = null
                value
            }
    }

    private fun incrementIndex() {
        index = (index + 1) % array.size
    }
}