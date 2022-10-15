package ru.spbstu.architecture.simulation.entities

sealed interface Request {
    val sourceIndex: Int
    val producedAt: Double

    sealed interface LeftBuffer : Request {
        val leftBufferAt: Double
    }

    sealed interface WasInDevice : LeftBuffer {
        val deviceIndex: Int
    }

    data class Waiting(
        override val sourceIndex: Int,
        override val producedAt: Double,
        val onDeny: () -> Unit
    ) : Request

    data class Denied(
        override val sourceIndex: Int,
        override val producedAt: Double,
        override val leftBufferAt: Double
    ) : LeftBuffer

    data class BeingProcessed(
        override val sourceIndex: Int,
        override val producedAt: Double,
        override val leftBufferAt: Double,
        override val deviceIndex: Int
    ) : WasInDevice

    data class Processed(
        override val sourceIndex: Int,
        override val producedAt: Double,
        override val leftBufferAt: Double,
        override val deviceIndex: Int,
        val processedAt: Double
    ) : WasInDevice
}

fun Request.Waiting.denied(at: Double) =
    Request.Denied(sourceIndex, producedAt, at).also { onDeny() }

fun Request.Waiting.beingProcessed(deviceIndex: Int, at: Double) =
    Request.BeingProcessed(sourceIndex, producedAt, at, deviceIndex)

fun Request.BeingProcessed.processed(at: Double) =
    Request.Processed(sourceIndex, producedAt, leftBufferAt, deviceIndex, at)