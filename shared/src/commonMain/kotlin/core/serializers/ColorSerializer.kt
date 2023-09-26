package core.serializers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ColorSerializer: KSerializer<Color> {

    override val descriptor: SerialDescriptor
        get() = ColorSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Color {
        val surrogate = decoder.decodeSerializableValue(ColorSurrogate.serializer())
        return Color(surrogate.argb)
    }

    override fun serialize(encoder: Encoder, value: Color) {
        val surrogate = ColorSurrogate(value.toArgb())
        encoder.encodeSerializableValue(ColorSurrogate.serializer(), surrogate)
    }

    @Serializable
    @SerialName("Color")
    private class ColorSurrogate(val argb: Int)
}