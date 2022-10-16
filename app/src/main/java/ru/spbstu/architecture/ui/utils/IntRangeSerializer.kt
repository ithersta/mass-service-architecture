package ru.spbstu.architecture.ui.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IntRangeSerializer : KSerializer<IntRange> {
    override val descriptor = PrimitiveSerialDescriptor("IntRange", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = decoder.decodeString().toIntRangeOrNull()!!

    override fun serialize(encoder: Encoder, value: IntRange) {
        encoder.encodeString(value.toString())
    }
}